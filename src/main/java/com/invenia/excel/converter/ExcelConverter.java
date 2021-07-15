package com.invenia.excel.converter;

import com.invenia.excel.converter.dto.ConvertResult;
import com.invenia.excel.converter.dto.Customer;
import com.invenia.excel.converter.dto.Item;
import com.invenia.excel.converter.dto.ItemCode;
import com.invenia.excel.converter.exception.UnregisteredCustomerException;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public void itemCodeConvert(String siteName) throws Exception {
		// 품목 엑셀 읽기
		List<ItemCode> itemCodes = readExcel(config.getTemplatePath() + "systemever/itemcode.xml",
				config.getItemFilePath());
		// 품목 중복 제거
		List<Item> distinctItems = Objects.requireNonNull(readItems(siteName))
				.stream()
				.filter(x -> itemCodes.stream().noneMatch(y -> x.getItemNo().equals(y.getItemCode())))
				.distinct()
				.collect(Collectors.toList());
		// 품목 엑셀 생성
		int size = makeExcel(distinctItems, Paths.get(getTemplateSitePath(siteName) + config.getItemFileName()),
				Paths.get(getOutputSitePath(siteName) + config.getItemFileName()));
		config.getConvertResult().get(siteName).setItemCodeResult(size);
	}

	public void contractOrderConvert(String siteName) throws Exception {
		// 수주 엑셀 생성
		int size = makeExcel(readItems(siteName), Paths.get(getTemplateSitePath(siteName) + config.getContractOrderFileName()),
				Paths.get(getOutputSitePath(siteName) + config.getContractOrderFileName()));
		config.getConvertResult().get(siteName).setContractOrderResult(size);
	}

	public void purchaseOrderConvert(String siteName) throws Exception {
		// 발주 엑셀 생성
		int size = makeExcel(readItems(siteName), Paths.get(getTemplateSitePath(siteName) + config.getPurchaseOrderFileName()),
				Paths.get(getOutputSitePath(siteName) + config.getPurchaseOrderFileName()));
		config.getConvertResult().get(siteName).setPurchaseOrderResult(size);
	}

	public void unregisteredCustomerCheck(String siteName) throws Exception {
		// 거래처 엑셀 읽기
		List<Customer> customers = readExcel(config.getTemplatePath() + "systemever/customer.xml",
				config.getCustomerFilePath());
		// 거래처 중복 제거
		List<Customer> distinctCustomers = Objects.requireNonNull(readItems(siteName))
				.stream()
				.filter(x -> !x.getCustName().equals(""))
				.filter(x -> customers.stream().noneMatch(y -> x.getCustName().equals(y.getCustName())))
				.map(x -> new Customer(x.getCustName()))
				.distinct()
				.collect(Collectors.toList());
		// 중복 거래처 발생시 Exception
		if (!distinctCustomers.isEmpty()) {
			// 거래처 엑셀 생성
			int result = makeExcel(distinctCustomers, Paths.get(getTemplateSitePath(siteName) + config.getCustomerFileName()),
					Paths.get(config.getOutputPath() + config.getCustomerFileName()));
			throw new UnregisteredCustomerException("미등록 거래처가 " + result + "건 존재합니다.");
		}
	}

	public void clearOutputPath() throws IOException {
		Path outputPath = Paths.get(config.getOutputPath());
		log.info(outputPath + " 삭제 실행");
		try (Stream<Path> walk = Files.walk(outputPath)) {
			walk.sorted(Comparator.reverseOrder())
					.forEach(x -> {
						try {
							Files.delete(x);
						} catch (IOException e) {
							log.error(e.getMessage(), e);
						}
					});
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		log.info(outputPath + " 삭제 완료");
	}

	private String getTemplateSitePath(String siteName) {
		return config.getTemplatePath() + siteName + "/";
	}

	private String getOutputSitePath(String siteName) {
		return config.getOutputPath() + siteName + "/";
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
			log.error(e.getMessage(), e);
			throw e;
		}
		return items.size();
	}

	private List<Item> readItems(String siteName) throws Exception {
		String templateSitePath = config.getTemplatePath() + siteName + "/";
		List<Item> items;

		if ("cozy".equals(siteName)) { // 코지
			items = convertHtmlToItem(Paths.get(config.getCozyFilePath()));
		} else if ("kd".equals(siteName)) { // KD 날짜 형식 변환
			items = readExcel(templateSitePath + "convert.xml", config.getKdFilePath());
			items.forEach(x -> x.setOrderDate(LocalDate.parse(x.getOrderDate())
					.format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
		} else { // 디디고
			items = readExcel(templateSitePath + "convert.xml", config.getMallFilePath());
		}
		return items;
	}

	private <S> List<S> readExcel(String xmlPath, String excelPath) throws IOException, InvalidFormatException, SAXException {
		List<S> items = new ArrayList<>();
		try (InputStream xmlInputStream = new BufferedInputStream(new FileInputStream(xmlPath))) {
			final XLSReader reader = ReaderBuilder.buildFromXML(xmlInputStream);
			try (InputStream xlsInputStream = new BufferedInputStream(new FileInputStream(excelPath))) {
				Map<String, Object> beans = new HashMap<>();
				beans.put("items", items);
				reader.read(xlsInputStream, beans);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		return items;
	}

	public void fileBackup(Long id) throws IOException {
		log.info("파일 백업 시작");
		Path backupPath = null;
		try {
			backupPath = Paths.get(config.getBackupPath()).resolve(String.valueOf(id));
			if (!Files.exists(backupPath)) {
				Files.createDirectories(backupPath);
			}
			// Source Backup
			fileMove(Paths.get(config.getChromeDownloadPath()), backupPath);
			fileMove(Paths.get(config.getIeDownloadPath()), backupPath);
			// Output Backup
			fileCopy(Paths.get(config.getOutputPath()), backupPath);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		log.info("파일 백업 완료");
	}

	public void demoFileCopy() throws IOException {
		fileCopy(Paths.get("C:/excel/demo/MemberOrderList.xls"), Paths.get(config.getIeDownloadPath()).resolve("MemberOrderList.xls"));
		fileCopy(Paths.get("C:/excel/demo/"), Paths.get(config.getChromeDownloadPath()));
	}

	private void fileMove(Path source, Path target) throws IOException {
		if (!Files.exists(source)) {
			Files.createDirectories(source);
		}
		Files.walk(source, 1)
				.filter(Files::isRegularFile)
				.filter(path -> path.getFileSystem().getPathMatcher("glob:**.{xls,xlsx,html}").matches(path))
				.forEach(x -> {
					try {
						Files.move(x, target.resolve(x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				});
	}

	private void fileCopy(Path source, Path target) throws IOException {
		if (Files.isDirectory(source)) {
			if (Files.notExists(target)) {
				Files.createDirectories(target);
			}
			try (Stream<Path> paths = Files.list(source)) {
				paths.forEach(p -> {
					try {
						fileCopy(p, target.resolve(source.relativize(p)));
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
				});
			}
		} else {
			// if file exists, replace it
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			log.debug(String.format("Copy File from \t'%s' to \t'%s'", source, target));
		}
	}

	public List<Item> convertHtmlToItem(Path xlsPath) throws IOException {
		Path htmlPath = xlsPath.resolveSibling(xlsPath.getFileName() + ".html");
		if (!Files.exists(htmlPath)) {
			Files.copy(xlsPath, htmlPath);
		}

		Document doc = Jsoup.parse(htmlPath.toFile(), "UTF-8");
		List<Item> items = new ArrayList<>();
		doc.select("tbody").select("tr")
				.stream()
				.filter(x -> !"".equals(x.children().get(0).text()) &&
						!x.children().get(0).text().contains("존재하지 않습니다"))
				.forEach(x -> {
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
}
