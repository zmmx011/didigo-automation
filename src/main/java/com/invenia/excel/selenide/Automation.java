package com.invenia.excel.selenide;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.files.FileFilters;
import com.invenia.excel.batch.BatchMail;
import com.invenia.excel.converter.ConvertConfig;
import com.invenia.excel.selenide.mall.MallPage;
import com.invenia.excel.selenide.systemever.CustomerInquiryFrame;
import com.invenia.excel.selenide.systemever.CustomerUploadFrame;
import com.invenia.excel.selenide.systemever.ItemInquiryFrame;
import com.invenia.excel.selenide.systemever.ItemUploadFrame;
import com.invenia.excel.selenide.systemever.LeftMenu;
import com.invenia.excel.selenide.systemever.LoginPage;
import com.invenia.excel.selenide.systemever.MainPage;
import com.invenia.excel.selenide.systemever.PurchaseOrderItemInquiryFrame;
import com.invenia.excel.selenide.systemever.PurchaseUnitPriceFrame;
import com.invenia.excel.selenide.systemever.SalesOrderInquiryFrame;
import com.invenia.excel.selenide.systemever.SalesOrderUploadFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Automation {

  private final ConvertConfig config;
  private final BatchMail mail;

  MainPage main = new MainPage();
  LeftMenu menu = new LeftMenu();

  public void setup() {
    System.setProperty("webdriver.chrome.driver", "C:/excel/driver/chrome_92.0.4515.43.exe");
    Configuration.fastSetValue = true;
    Configuration.timeout = 10000;
    Configuration.pageLoadTimeout = 300000;
    Configuration.fileDownload = FileDownloadMode.FOLDER;
    Configuration.downloadsFolder = config.getDownloadPath();
    Configuration.reportsFolder = config.getDownloadPath();
    Configuration.headless = true;
  }

  /**
   * ????????? ??? ????????????
   *
   * @param fromDate ?????? ?????????
   * @param toDate   ?????? ?????????
   */
  public void runMallDownload(LocalDate fromDate, LocalDate toDate, String url)
      throws IOException, InterruptedException {
    String jsCode = "  document.excelform.OPER_MALL_FG.value = \"ALL\";\n"
        + "  document.getElementById(\"contractStartDate_con\").value = arguments[0];\n"
        + "  document.getElementById(\"contractEndDate_con\").value = arguments[1];\n"
        + "  let h_sitefg = \"\";\n"
        + "  h_sitefg += \"<input type='hidden' name='SITE_FGS' id='SITE_FGS' value='021001' >\";\n"
        + "  h_sitefg += \"<input type='hidden' name='SITE_FGS' id='SITE_FGS' value='021002' >\";\n"
        + "  h_sitefg += \"<input type='hidden' name='SITE_FGS' id='SITE_FGS' value='021003' >\";\n"
        + "  document.getElementById(\"span_sitefg\").innerHTML = h_sitefg;\n"
        + "  document.excelform.CSTM_BCO_CD.value = document.getElementById(\"CSTM\").value;\n"
        + "  document.excelform.DPT_CD.value = document.getElementById(\"dept\").value;\n"
        + "  document.excelform.ACCNT_CD.value = document.getElementById(\"acct\").value;\n"
        + "  document.excelform.MBR_TP_CD.value = document.getElementById(\"MBR_TP_CD\").value;\n"
        + "  document.excelform.contractStartDate_con.value = unformat(document.getElementById(\"contractStartDate_con\").value);\n"
        + "  document.excelform.contractEndDate_con.value = unformat(document.getElementById(\"contractEndDate_con\").value);\n"
        + "  document.excelform.action = \"simpleAction.do\";\n"
        + "  document.excelform.target = \"_self\";\n"
        + "  document.getElementById(\"actionKey\").value = \"search\";\n"
        + "  document.getElementById(\"resultType\").value = \"EXCEL\";\n"
        + "  document.getElementById(\"queryKey\").value = \"ord.tod0010m.select.mbrAccount.excel\";\n"
        + "  document.getElementById(\"fileName\").value = \"MemberOrderList\";\n"
        + "  document.excelform.submit();";

    newTab();
    Selenide.open(url);
    // ?????????
    MallPage mallPage = new MallPage();
    mallPage.loginId.sendKeys("it01");
    mallPage.loginPwd.sendKeys("qlalfqjsgh!@34");
    mallPage.loginBtn.click();

    Selenide.open("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    String from = fromDate.format(dateFormat);
    String to = toDate.format(dateFormat);

    Selenide.executeJavaScript(jsCode, from, to);
    Thread.sleep(5000);

    Path downloadsPath = Paths.get(config.getDownloadPath());
    Path xlsPath;
    try (Stream<Path> walk = Files.walk(downloadsPath)) {
      xlsPath = walk.filter(Files::isRegularFile)
          .filter(path -> path.getFileSystem().getPathMatcher("glob:**.{xls}").matches(path))
          .findFirst().orElseThrow(() -> new FileNotFoundException("MemberOrderList.xls not found"));
    }

    Files.move(xlsPath, downloadsPath.resolve(xlsPath.getFileName()), StandardCopyOption.ATOMIC_MOVE);
    log.info("????????? ??? ???????????? ?????? ");
    Selenide.switchTo().window(0);
  }

  /**
   * KD ????????????
   *
   * @param fromDate ?????? ?????????
   * @param toDate   ?????? ?????????
   */
  public void runKdDownload(LocalDate fromDate, LocalDate toDate, String url) throws IOException {
    newTab();
    runErpLogin(url, "m_itsecurity@inveniacorp.com", "Invenia_0041");

    // ?????? ?????? - ?????? ?????? ?????? - ?????? ?????? - ?????? ?????? ?????? ??????
    menu.purchaseModule.click();
    menu.purchaseActivityMngMenu.click();
    menu.purchaseOrderGroup.click();
    menu.purchaseOrderItemInquiry.click();
    main.loadingPage.shouldNot(Condition.visible);

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    String from = fromDate.format(dateFormat);
    String to = toDate.format(dateFormat);

    // ?????? ????????????
    PurchaseOrderItemInquiryFrame frame = new PurchaseOrderItemInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.customer.val("?????????");
    frame.purchaseOrderDateFrom.val(fromDate.format(dateFormat));
    frame.purchaseOrderDateTo.val(toDate.format(dateFormat));
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    String moveFilePath = moveDownloadFile(downloadFile);
    log.info("KD ???????????? ?????? : {}", moveFilePath);

    Selenide.switchTo().window(0);
  }

  /**
   * ERP ?????????
   *
   * @param url ERP ???????????? ??????
   */
  public void runErpLogin(String url) {
    runErpLogin(url, "d_itsecurity@inveniacorp.com", "inveni@2021");
  }

  public void runErpLogin(String url, String id, String pw) {
    LoginPage page = new LoginPage();
    Selenide.open(url);
    page.loginBg.shouldBe(Condition.visible);
    Selenide.executeJavaScript("document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");
    page.loginId.val(id);
    page.loginPwd.val(pw);
    page.loginBtn.click();
  }

  /**
   * ?????? ????????????
   */
  public void runItemCodeDownload() throws IOException {
    // ?????? ?????? - ?????? ?????? - ?????? ?????? - ?????? ??????
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.item.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ?????? ????????????
    ItemInquiryFrame frame = new ItemInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    String moveFilePath = moveDownloadFile(downloadFile);
    log.info("?????? ???????????? ?????? : {}", moveFilePath);
    Selenide.switchTo().defaultContent();
  }

  /**
   * ?????? ?????? ????????????
   */
  public void runItemPriceDownload() throws IOException {
    // ?????? ?????? - ?????? ?????? ?????? - ?????? ?????? - ?????? ?????? ??????
    menu.purchaseModule.click();
    menu.purchaseMasterDataMenu.click();
    menu.purchaseUnitPriceGroup.click();
    menu.purchaseUnitPrice.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ?????? ?????? ????????????
    PurchaseUnitPriceFrame frame = new PurchaseUnitPriceFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    String moveFilePath = moveDownloadFile(downloadFile);
    log.info("?????? ?????? ???????????? ?????? : {}", moveFilePath);
    Selenide.switchTo().defaultContent();
  }

  /**
   * ????????? ????????????
   */
  public void runCustomerDownload() throws IOException {
    // ?????? ?????? - ????????? ?????? - ????????? ?????? - ????????? ??????
    menu.corporateModule.click();
    menu.customerMenu.click();
    menu.customerGroup.click();
    menu.customerInquiry.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ????????? ????????????
    CustomerInquiryFrame frame = new CustomerInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.inquiryBtn.click();
    main.loadingPage.shouldNot(Condition.visible);
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    String moveFilePath = moveDownloadFile(downloadFile);
    log.info("????????? ???????????? ?????? : {}", moveFilePath);
    Selenide.switchTo().defaultContent();
  }

  /**
   * ?????? ????????????
   *
   * @param fromDate ?????? ?????????
   * @param toDate   ?????? ?????????
   */
  public void runContractOrderDownload(LocalDate fromDate, LocalDate toDate) throws IOException {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ?????? ?????? - ?????? ?????? - ??????  - ?????? ??????
    menu.salesModule.click();
    menu.salesOrderMenu.click();
    menu.salesOrderGroup.click();
    menu.salesOrderInquiry.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ?????? ????????????
    SalesOrderInquiryFrame frame = new SalesOrderInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.bizUnit.selectOption("?????????");
    frame.salesOrderType.selectOption("???????????????");
    frame.salesOrderDateFrom.val(fromDate.minusMonths(3).format(dateFormat));
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    String moveFilePath = moveDownloadFile(downloadFile);
    log.info("?????? ???????????? ?????? : {}", moveFilePath);
    Selenide.switchTo().defaultContent();
  }

  /**
   * ?????? ?????????
   *
   * @param siteName ?????? ????????? ???
   */
  public void runItemCodeUpload(String siteName) throws InterruptedException {
    int size = config.getConvertResult().get(siteName).getItemCodeSize();
    log.info("?????? ?????? ?????? {} ???", size);
    if (size <= 0) {
      return;
    }
    // ?????? ?????? - ?????? ?????? - ?????? ?????? - ?????? ?????? ?????????
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.itemUpload.click();
    main.loadingPage.shouldNot(Condition.visible);
    // ?????? ?????????
    ItemUploadFrame frame = new ItemUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    Path uploadFile = Paths.get(config.getOutputPath(siteName, config.getItemCodeFileName()));
    frame.file.uploadFile(uploadFile.toFile());
    frame.getDataBtn.click();
    Thread.sleep(5000);
    frame.saveBtn.click();
    Thread.sleep(5000);
    Selenide.switchTo().defaultContent();
    main.msgBtnOk.click();
    log.info("{} ?????? ?????? ????????? ??????", siteName);
  }

  /**
   * ?????? ?????????
   *
   * @param siteName ?????? ????????? ???
   */
  public void runItemPriceUpload(String siteName) throws InterruptedException {
    long size = config.getConvertResult().get(siteName).getItemPriceSize();
    log.info("?????? ?????? ?????? {} ???", size);
    if (size <= 0) {
      return;
    }
    // ?????? ?????? - ?????? ?????? ?????? - ?????? ?????? - ?????? ?????? ??????
    menu.purchaseModule.click();
    menu.purchaseMasterDataMenu.click();
    menu.purchaseUnitPriceGroup.click();
    menu.purchaseUnitPrice.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ?????? ?????? ?????????
    PurchaseUnitPriceFrame frame = new PurchaseUnitPriceFrame();
    Selenide.switchTo().frame(frame.frame);

    String jsCode = "let evt = new ClipboardEvent('paste', {clipboardData: new DataTransfer()});\n"
        + "evt.clipboardData.setData('Text', arguments[0]);\n"
        + "return document.getElementById(\"SS\").dispatchEvent(evt);";

    String data = config.getConvertResult().get(siteName).getItemPriceData();

    Selenide.executeJavaScript(jsCode, data);
    Thread.sleep(5000);
    frame.saveBtn.click();
    Thread.sleep(5000);
    Selenide.switchTo().defaultContent();
    log.info("{} ?????? ????????? ??????", siteName);
  }

  /**
   * ????????? ?????????
   *
   * @param siteName ?????? ????????? ???
   */
  public void runCustomerUpload(String siteName) throws InterruptedException {
    int size = config.getConvertResult().get(siteName).getCustomerSize();
    log.info("????????? ?????? ?????? {} ???", size);
    if (size <= 0) {
      return;
    }
    // ?????? ?????? - ????????? ?????? - ????????? ?????? - ????????? ??????
    menu.corporateModule.click();
    menu.customerMenu.click();
    menu.customerGroup.click();
    menu.customerUpload.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ????????? ?????????
    CustomerUploadFrame frame = new CustomerUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    Path uploadFile = Paths.get(config.getOutputPath(siteName, config.getCustomerFileName()));
    frame.file.uploadFile(uploadFile.toFile());
    frame.getDataBtn.click();
    Thread.sleep(5000);
    frame.saveBtn.click();
    Thread.sleep(5000);
    Selenide.switchTo().defaultContent();
    log.info("{} ????????? ????????? ??????", siteName);

    // ????????? ?????? ??????
    mail.sendUnregisteredCustomerMail(uploadFile.toString(), size);
  }

  /**
   * ?????? ?????????
   *
   * @param siteName ?????? ????????? ???
   */
  public void runContractOrderUpload(String siteName) throws InterruptedException {
    int size = config.getConvertResult().get(siteName).getContractOrderSize();
    log.info("?????? ?????? ?????? {} ???", size);
    if (size <= 0) {
      return;
    }
    // ?????? ?????? - ?????? ?????? - ??????  - ?????? ?????????
    menu.salesModule.click();
    menu.salesOrderMenu.click();
    menu.salesOrderGroup.click();
    menu.salesOrderUpload.click();
    main.loadingPage.shouldNot(Condition.visible);

    // ?????? ?????????
    SalesOrderUploadFrame frame = new SalesOrderUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.empName.val("?????????");
    frame.getFileBtn.click();
    Path uploadFile = Paths.get(config.getOutputPath(siteName, config.getContractOrderFileName()));
    frame.file.uploadFile(uploadFile.toFile());
    frame.getDataBtn.click();
    Thread.sleep(5000);
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    log.info("{} ?????? ????????? ??????", siteName);

    Thread.sleep(10000);
  }

  public void newTab() {
    WebDriver driver = getWebDriver();
    // ??? ??? ??????
    Selenide.executeJavaScript("window.open('about:blank','_blank');");

    // ??? ?????? ????????????
    List<String> tabs = new ArrayList<>(driver.getWindowHandles());

    // ??? ??????
    Selenide.switchTo().window(tabs.get(tabs.size() - 1));
  }

  public void quitAutomation() {
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    Selenide.screenshot(LocalDateTime.now().format(dateFormat));
    Selenide.closeWebDriver();
    log.info("Chrome Driver Quit");
  }

  public String moveDownloadFile(File downloadFile) throws IOException {
    Path downloadFilePath = Paths.get(downloadFile.getAbsolutePath());
    Path moveFilePath = Paths.get(config.getDownloadPath() + downloadFilePath.getFileName());
    Files.move(downloadFilePath, moveFilePath, StandardCopyOption.REPLACE_EXISTING);
    return moveFilePath.toAbsolutePath().toString();
  }
}
