package com.invenia.excel.selenide.systemever;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.files.FileFilters;
import com.invenia.excel.selenide.canvas.IsCanvasSame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SystemEverTests {

  private static final Logger log = LoggerFactory.getLogger(SystemEverTests.class);

  MainPage main = new MainPage();
  LeftMenu menu = new LeftMenu();

  @BeforeAll
  public static void setup() {
    System.setProperty("java.awt.headless", "false");
    Configuration.fastSetValue = true;
    Configuration.timeout = 10000;
    //Configuration.headless = true;
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = FileDownloadMode.PROXY;
    login();
  }

  @AfterAll
  public static void close() {
    Selenide.closeWebDriver();
  }

  @AfterEach
  public void closeTab() throws InterruptedException {
    main.tabCloseButton.click();
    Thread.sleep(2000);
    if (main.msgBtnOk.isDisplayed()) {
      main.msgBtnOk.click();
    }
  }

  public static void login() {
    LoginPage page = new LoginPage();
    Selenide.open("https://mfg.systemevererp.com/");
    Selenide.executeJavaScript("document.querySelectorAll('.popuploginPage').forEach(el => el.remove());");
    page.loginId.val("d_itsecurity@inveniacorp.com");
    page.loginPwd.val("inveni@2021");
    page.loginBtn.click();
  }


  @DisplayName("품목 다운로드")
  @Order(1)
  public void itemCodeDownloadTest() throws FileNotFoundException {
    // 전사 관리 - 품목 관리 - 품목 등록 - 품목 조회
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.item.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 품목 다운로드
    ItemInquiryFrame frame = new ItemInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    BufferedImage image = frame.canvas.screenshotAsImage();
    frame.inquiryBtn.click();
    frame.canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    assertTrue(downloadFile.exists());
    log.info("품목 다운로드 완료 : " + downloadFile.getAbsolutePath());
    Selenide.switchTo().defaultContent();
  }


  @DisplayName("구매 단가 다운로드")
  @Order(2)
  public void itemPriceDownloadTest() throws FileNotFoundException {
    // 구매 관리 - 구매 기준 정보 - 구매 단가 - 구매 단가 등록
    menu.purchaseModule.click();
    menu.purchaseMasterDataMenu.click();
    menu.purchaseUnitPriceGroup.click();
    menu.purchaseUnitPrice.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 구매 단가 다운로드
    PurchaseUnitPriceFrame frame = new PurchaseUnitPriceFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    frame.newBtn.click();
    assertTrue(downloadFile.exists());
    log.info("구매 단가 다운로드 완료 : " + downloadFile.getAbsolutePath());
    Selenide.switchTo().defaultContent();
  }


  @DisplayName("거래처 다운로드")
  @Order(3)
  public void customerDownloadTest() throws FileNotFoundException {
    // 전사 관리 - 거래처 관리 - 거래처 등록 - 거래처 조회
    menu.corporateModule.click();
    menu.customerMenu.click();
    menu.customerGroup.click();
    menu.customerInquiry.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 거래처 다운로드
    CustomerInquiryFrame frame = new CustomerInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.inquiryBtn.click();
    main.loadingPage.shouldNot(Condition.visible);
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    assertTrue(downloadFile.exists());
    log.info("거래처 다운로드 완료 : " + downloadFile.getAbsolutePath());
    Selenide.switchTo().defaultContent();
  }


  @DisplayName("수주 다운로드")
  @Order(4)
  public void salesOrderDownloadTest() throws FileNotFoundException {
    // 영업 관리 - 수주 관리 - 수주  - 수주 조회
    menu.salesModule.click();
    menu.salesOrderMenu.click();
    menu.salesOrderGroup.click();
    menu.salesOrderInquiry.click();
    main.loadingPage.shouldNot(Condition.visible);

    LocalDate now = LocalDate.now();

    // 수주 다운로드
    SalesOrderInquiryFrame frame = new SalesOrderInquiryFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.bizUnit.selectOption("디디고");
    frame.salesOrderType.selectOption("온라인영업");
    frame.salesOrderDateFrom.val(now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    frame.deliveryDateFrom.val(now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    frame.deliveryDateTo.val(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    frame.inquiryBtn.click();
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    assertTrue(downloadFile.exists());
    log.info("수주 다운로드 완료 : " + downloadFile.getAbsolutePath());
    Selenide.switchTo().defaultContent();
  }


  @DisplayName("품목 업로드")
  @Order(5)
  public void itemCodeUploadTest() throws IOException {
    // 전사 관리 - 품목 관리 - 품목 등록 - 품목 등록 업로드
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.itemUpload.click();
    main.loadingPage.shouldNot(Condition.visible);
    // 품목 업로드
    ItemUploadFrame frame = new ItemUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    frame.file.uploadFile(new ClassPathResource("selenide/sample/RptWDAItemUpload.xlsx").getFile());
    BufferedImage image = frame.canvas.screenshotAsImage();
    frame.getDataBtn.click();
    frame.canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    // 품목 자산 분류 미입력으로 실패 되어야 한다.
    assertTrue(main.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
    main.msgBtnOk.click();
  }

  @Test
  @DisplayName("거래처 업로드")
  @Order(6)
  public void customerUploadTest() throws IOException {
    // 전사 관리 - 거래처 관리 - 거래처 등록 - 거래처 조회
    menu.corporateModule.click();
    menu.customerMenu.click();
    menu.customerGroup.click();
    menu.customerUpload.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 거래처 업로드
    CustomerUploadFrame frame = new CustomerUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    frame.file.uploadFile(new ClassPathResource("selenide/sample/RptWDACustUpload.xlsx").getFile());
    BufferedImage image = frame.canvas.screenshotAsImage();
    frame.getDataBtn.click();
    frame.canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    // 거래처 종류 미입력으로 실패 되어야 한다.
    assertTrue(frame.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
    frame.msgBtnOk.click();
  }

  @Test
  @DisplayName("구매 단가 등록")
  @Order(7)
  public void itemPriceUploadTest() throws InterruptedException {
    // 구매 관리 - 구매 기준 정보 - 구매 단가 - 구매 단가 등록
    menu.purchaseModule.click();
    menu.purchaseMasterDataMenu.click();
    menu.purchaseUnitPriceGroup.click();
    menu.purchaseUnitPrice.click();
    Thread.sleep(5000);
    main.loadingPage.shouldNot(Condition.visible);

    // 구매 단가 업로드
    PurchaseUnitPriceFrame frame = new PurchaseUnitPriceFrame();
    Selenide.switchTo().frame(frame.frame);

    String sampleData = "\tL000566248\t(주)까사벨라\t225,000.00\t내수\t2021-07-30\r\n"
        + "\tL000682408\t(주)넷케이티아이\t16,000.00\t내수\t2021-07-30\r\n";

    String jsCode = "let evt = new ClipboardEvent('paste', {clipboardData: new DataTransfer()});\n"
        + "evt.clipboardData.setData('Text', arguments[0]);\n"
        + "return document.getElementById(\"SS\").dispatchEvent(evt);";

    BufferedImage image = frame.canvas.screenshotAsImage();
    Boolean dispatchEvent = Selenide.executeJavaScript(jsCode, sampleData);
    frame.canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
    assertEquals(Boolean.TRUE, dispatchEvent);
    Selenide.switchTo().defaultContent();
  }

  @Test
  @DisplayName("수주 업로드")
  @Order(8)
  public void salesOrderUploadTest() throws IOException {
    // 영업 관리 - 수주 관리 - 수주  - 수주 업로드
    menu.salesModule.click();
    menu.salesOrderMenu.click();
    menu.salesOrderGroup.click();
    menu.salesOrderUpload.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 수주 업로드
    SalesOrderUploadFrame frame = new SalesOrderUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    frame.file.uploadFile(new ClassPathResource("selenide/sample/RptWSLOrderUpload.xlsx").getFile());
    BufferedImage image = frame.canvas.screenshotAsImage();
    frame.getDataBtn.click();
    frame.canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    // 거래처 미입력으로 실패 되어야 한다.
    assertTrue(main.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
    main.msgBtnOk.click();
  }
}
