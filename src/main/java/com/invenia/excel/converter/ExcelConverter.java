package com.invenia.excel.converter;

import com.invenia.excel.common.AnsiColorEscapeSequence;
import com.invenia.excel.converter.dto.ConvertResult;
import com.invenia.excel.converter.dto.Customer;
import com.invenia.excel.converter.dto.Item;
import com.invenia.excel.converter.dto.ItemCode;
import com.invenia.excel.converter.dto.Price;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jxls.common.Context;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReader;
import org.jxls.util.JxlsHelper;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelConverter {

  private final ConvertConfig config;

  public void init() {
    config.setConvertResult(new HashMap<>());
    config.getConvertResult().put("cozy", new ConvertResult());
    config.getConvertResult().put("kd", new ConvertResult());
    config.getConvertResult().put("mall", new ConvertResult());
  }

  public void itemConvert(String siteName) throws Exception {
    // 사이트 데이터
    List<Item> items = Objects.requireNonNull(readItems(siteName));

    // 품목 엑셀 읽기
    List<ItemCode> itemCodes =
        readExcel(config.getTemplatePath("systemever/itemcode.xml"), config.getItemCodeFilePath());
    // 품목 중복 제거
    List<Item> filteredItems =
        items.stream()
            .distinct()
            .filter(
                item ->
                    itemCodes.stream()
                        .parallel()
                        .noneMatch(itemCode -> item.getItemNo().equals(itemCode.getItemCode())))
            .collect(Collectors.toList());
    // 품목 엑셀 생성
    String itemCodeFileName = config.getItemCodeFileName();
    int itemCodeSize =
        makeExcel(
            filteredItems,
            Paths.get(config.getTemplatePath(siteName, itemCodeFileName)),
            Paths.get(config.getOutputPath(siteName, itemCodeFileName)));

    config.getConvertResult().get(siteName).setItemCodeSize(itemCodeSize);

    // 클립보드 초기화
    StringSelection clipboardData = new StringSelection("");
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(clipboardData, null);

    // 단가 엑셀 읽기
    List<Price> prices =
        readExcel(config.getTemplatePath("systemever/price.xml"), config.getPriceFilePath());

    // 단가 중복 제거
    String filteredPrice =
        items.stream()
            .distinct()
            .filter(
                item ->
                    !Objects.equals(
                        item.getPrice(),
                        prices.stream()
                            .parallel()
                            .filter(price -> price.getItemCode().equals(item.getItemNo()))
                            .findAny()
                            .orElseGet(Price::new)
                            .getPrice()))
            .map(
                item ->
                    "\t"
                        + item.getItemNo()
                        + "\t"
                        + item.getDvPlaceName()
                        + "\t"
                        + item.getPrice()
                        + "\t내수\t"
                        + LocalDate.now())
            .collect(Collectors.joining("\r\n"));

    // 클립보드로 전송
    clipboardData = new StringSelection(filteredPrice);
    clipboard.setContents(clipboardData, null);

    config
        .getConvertResult()
        .get(siteName)
        .setItemPriceSize(filteredPrice.chars().parallel().filter(c -> c == '\n').count());
  }

  public void contractOrderConvert(String siteName) throws Exception {
    // 수주 엑셀 읽기
    List<Item> orders =
        readExcel(
            config.getTemplatePath("systemever/contractorder.xml"),
            config.getContractOrderFilePath());
    log.debug(AnsiColorEscapeSequence.RED.es() + "수주 조회 : " + orders.size());
    // 수주 중복 제거
    List<Item> distinctOrders =
        Objects.requireNonNull(readItems(siteName)).stream()
            .filter(
                item ->
                    orders.stream()
                        .filter(order -> !order.getRemarkM().equals(""))
                        .noneMatch(order -> item.getRemarkM().equals(order.getRemarkM())))
            .collect(Collectors.toList());
    // 수주 엑셀 생성
    String contractOrderFileName = config.getContractOrderFileName();
    int size =
        makeExcel(
            distinctOrders,
            Paths.get(config.getTemplatePath(siteName, contractOrderFileName)),
            Paths.get(config.getOutputPath(siteName, contractOrderFileName)));

    config.getConvertResult().get(siteName).setContractOrderSize(size);

    log.debug(AnsiColorEscapeSequence.RED.es() + "수주 중복 제거 완료 : " + size);
  }

  public void customerConvert(String siteName) throws Exception {
    // 거래처 엑셀 읽기
    List<Customer> customers =
        readExcel(config.getTemplatePath("systemever/customer.xml"), config.getCustomerFilePath());

    // 사이트 데이터 읽기
    List<Item> items = readItems(siteName);

    // 미등록 거래처
    List<Customer> unregisteredCustomers =
        Objects.requireNonNull(items).stream()
            .filter(item -> !item.getCustName().equals(""))
            .filter(
                item ->
                    customers.stream()
                        .noneMatch(customer -> item.getCustName().equals(customer.getCustName())))
            .map(item -> new Customer(item.getCustName()))
            .distinct()
            .collect(Collectors.toList());

    log.info(
        AnsiColorEscapeSequence.MAGENTA.es() + "미등록 거래처 " + unregisteredCustomers.size() + "건");

    // 미등록 공급사
    List<Customer> unregisteredSuppliers =
        Objects.requireNonNull(items).stream()
            .filter(
                item ->
                    customers.stream()
                        .noneMatch(
                            customer -> item.getDvPlaceName().equals(customer.getCustName())))
            .map(item -> new Customer(item.getDvPlaceName()))
            .distinct()
            .collect(Collectors.toList());

    log.info(
        AnsiColorEscapeSequence.MAGENTA.es() + "미등록 공급사 " + unregisteredSuppliers.size() + "건");

    unregisteredCustomers.addAll(unregisteredSuppliers);

    // 거래처 엑셀 생성
    String customerFileName = config.getCustomerFileName();
    int size =
        makeExcel(
            unregisteredCustomers,
            Paths.get(config.getTemplatePath(siteName, customerFileName)),
            Paths.get(config.getOutputPath(siteName, customerFileName)));

    config.getConvertResult().get(siteName).setCustomerSize(size);
  }

  private int makeExcel(List<?> items, Path templatePath, Path outputPath) throws IOException {
    if (!Files.exists(outputPath)) {
      Files.createDirectories(outputPath.getParent());
    }
    try (InputStream is = Files.newInputStream(templatePath)) {
      try (OutputStream os = Files.newOutputStream(outputPath)) {
        Context context = new Context();
        context.putVar("items", items);
        JxlsHelper.getInstance().processTemplate(is, os, context);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
      throw e;
    }
    return items.size();
  }

  private List<Item> readItems(String siteName) throws Exception {
    List<Item> items;
    String templatePath = config.getTemplatePath(siteName, "convert.xml");
    if ("cozy".equals(siteName)) { // 코지
      items = convertHtmlToItem(Paths.get(config.getCozyFilePath()));
    } else if ("kd".equals(siteName)) { // KD 날짜 형식 변환
      items = readExcel(templatePath, config.getKdFilePath());
      items.forEach(
          x ->
              x.setOrderDate(
                  LocalDate.parse(x.getOrderDate())
                      .format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
    } else { // 디디고
      items = readExcel(templatePath, config.getMallFilePath());
    }
    return items;
  }

  private <S> List<S> readExcel(String xmlPath, String excelPath)
      throws IOException, InvalidFormatException, SAXException {
    List<S> items = new ArrayList<>();
    try (InputStream xmlInputStream = new BufferedInputStream(new FileInputStream(xmlPath))) {
      final XLSReader reader = ReaderBuilder.buildFromXML(xmlInputStream);
      try (InputStream xlsInputStream = new BufferedInputStream(new FileInputStream(excelPath))) {
        Map<String, Object> beans = new HashMap<>();
        beans.put("items", items);
        reader.read(xlsInputStream, beans);
      } catch (Exception e) {
        log.error(e.getLocalizedMessage(), e);
        throw e;
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
      throw e;
    }
    return items;
  }

  public List<Item> convertHtmlToItem(Path xlsPath) throws IOException {
    Path htmlPath = xlsPath.resolveSibling(xlsPath.getFileName() + ".html");
    if (!Files.exists(htmlPath)) {
      Files.copy(xlsPath, htmlPath);
    }

    Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
    List<Item> items = new ArrayList<>();
    doc.select("tbody").select("tr").stream()
        .filter(
            x ->
                !"".equals(x.children().get(0).text())
                    && !x.children().get(0).text().contains("존재하지 않습니다"))
        .forEach(
            x -> {
              Elements elements = x.children();
              Item item = new Item();
              item.setCustName("인베니아");
              item.setIdxNo(elements.get(0).text());
              item.setOrderDate(elements.get(2).text().replace("-", ""));
              item.setItemNo(elements.get(4).text());
              item.setItemName(elements.get(4).text());
              item.setQty(elements.get(6).text());
              item.setPrice(elements.get(7).text());
              items.add(item);
            });
    return items;
  }

  public void fileBackup(Long id) throws IOException {
    log.info("파일 백업 시작");
    try {
      Path backupPath = Paths.get(config.getBackupPath(String.valueOf(id)));
      Path chromeDownloadPath = Paths.get(config.getChromeDownloadPath());
      Path ieDownloadPath = Paths.get(config.getIeDownloadPath());
      Path outputPath = Paths.get(config.getOutputPath());
      // make backup folder
      if (!Files.exists(backupPath)) {
        Files.createDirectories(backupPath);
      }
      // Chrome Source Backup
      if (Files.exists(chromeDownloadPath)) {
        fileMove(chromeDownloadPath, backupPath);
      }
      // IE Source Backup
      if (Files.exists(ieDownloadPath)) {
        fileMove(ieDownloadPath, backupPath);
      }
      // Output Backup
      if (Files.exists(outputPath)) {
        fileCopy(outputPath, backupPath);
      }
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
      throw e;
    }
    log.info("파일 백업 완료");
  }

  private void fileMove(Path source, Path target) throws IOException {
    if (!Files.exists(source)) {
      Files.createDirectories(source);
    }
    Files.walk(source, 1)
        .filter(Files::isRegularFile)
        .filter(
            path -> path.getFileSystem().getPathMatcher("glob:**.{xls,xlsx,html}").matches(path))
        .forEach(
            x -> {
              try {
                Files.move(x, target.resolve(x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
              }
            });
  }

  private void fileCopy(Path source, Path target) throws IOException {
    if (Files.isDirectory(source)) {
      if (Files.notExists(target)) {
        Files.createDirectories(target);
      }
      try (Stream<Path> paths = Files.list(source)) {
        paths.forEach(
            p -> {
              try {
                fileCopy(p, target.resolve(source.relativize(p)));
              } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
              }
            });
      }
    } else {
      // if file exists, replace it
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      log.debug(String.format("Copy File from \t'%s' to \t'%s'", source, target));
    }
  }

  public void clearOutputPath() throws IOException {
    Path outputPath = Paths.get(config.getOutputPath());
    if (Files.exists(outputPath)) {
      log.info(outputPath + " 삭제 실행");
      try (Stream<Path> walk = Files.walk(outputPath)) {
        walk.sorted(Comparator.reverseOrder())
            .forEach(
                x -> {
                  try {
                    Files.delete(x);
                  } catch (IOException e) {
                    log.error(e.getLocalizedMessage(), e);
                  }
                });
      } catch (IOException e) {
        log.error(e.getLocalizedMessage(), e);
        throw e;
      }
      log.info(outputPath + " 삭제 완료");
    }
  }
}
