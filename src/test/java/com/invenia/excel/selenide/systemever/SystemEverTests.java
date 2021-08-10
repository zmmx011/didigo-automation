package com.invenia.excel.selenide.systemever;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilters;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
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
import org.openqa.selenium.Keys;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class SystemEverTests {

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
  public void defaultContent() {
    Selenide.switchTo().defaultContent();
  }

  public static void login() {
    LoginPage page = new LoginPage();
    Selenide.open("https://mfg.systemevererp.com/");
    Selenide.executeJavaScript("document.querySelectorAll('.popuploginPage').forEach(el => el.remove());");
    page.loginId.val("d_itsecurity@inveniacorp.com");
    page.loginPwd.val("inveni@2021");
    page.loginBtn.click();
    //Selenide.executeJavaScript("document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");
  }

  @Test
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
    waitUntilCanvasLoad(frame.canvas, frame.inquiryBtn);
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    Selenide.switchTo().defaultContent();
    assertTrue(downloadFile.exists());
  }

  @Test
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
    Selenide.switchTo().defaultContent();
    assertTrue(downloadFile.exists());
  }

  @Test
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
    Selenide.switchTo().defaultContent();
  }

  @Test
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
    Selenide.switchTo().defaultContent();
    assertTrue(downloadFile.exists());
  }

  @Test
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
    enterEscapeKey();
    frame.file.uploadFile(new ClassPathResource("selenide/sample/RptWDAItemUpload.xlsx").getFile());
    waitUntilCanvasLoad(frame.canvas, frame.getDataBtn);
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    // 품목 자산 분류 미입력으로 실패 되어야 한다.
    assertTrue(frame.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
    frame.msgBtnOk.click();
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
    enterEscapeKey();
    frame.file.uploadFile(new ClassPathResource("selenide/sample/RptWDACustUpload.xlsx").getFile());
    waitUntilCanvasLoad(frame.canvas, frame.getDataBtn);
    frame.saveBtn.click();
    Selenide.switchTo().defaultContent();
    // 거래처 종류 미입력으로 실패 되어야 한다.
    assertTrue(frame.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
    frame.msgBtnOk.click();
  }

  @Test
  @DisplayName("구매 단가 등록")
  @Order(7)
  public void itemPriceUploadTest() throws InterruptedException, AWTException {
    // 구매 관리 - 구매 기준 정보 - 구매 단가 - 구매 단가 등록
    menu.purchaseModule.click();
    menu.purchaseMasterDataMenu.click();
    menu.purchaseUnitPriceGroup.click();
    menu.purchaseUnitPrice.click();
    main.loadingPage.shouldNot(Condition.visible);

    // 구매 단가 업로드
    PurchaseUnitPriceFrame frame = new PurchaseUnitPriceFrame();
    Selenide.switchTo().frame(frame.frame);

    StringSelection data =
        new StringSelection(
            "[르크루제] 원형 무쇠 냄비 22cm [색상 : 카시스]\tL000566248\t(주)까사벨라\t225,000.00\t내수\t2021-07-30\n");
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(data, data);

    frame.canvas.click();
    frame.canvas.sendKeys(Keys.chord(Keys.CONTROL, "v"));
    //Selenide.actions().sendKeys(frame.canvas, Keys.chord(Keys.SHIFT, Keys.INSERT));

/*    Robot robot = new Robot();
    Thread.sleep(2000);
    robot.keyPress(KeyEvent.VK_CONTROL);
    robot.keyPress(KeyEvent.VK_LEFT);
    robot.keyRelease(KeyEvent.VK_LEFT);
    robot.keyPress(KeyEvent.VK_UP);
    robot.keyRelease(KeyEvent.VK_UP);
    robot.keyRelease(KeyEvent.VK_CONTROL);
    robot.keyPress(KeyEvent.VK_RIGHT);
    robot.keyRelease(KeyEvent.VK_RIGHT);
    robot.keyPress(KeyEvent.VK_CONTROL);
    robot.keyPress(KeyEvent.VK_V);
    robot.keyRelease(KeyEvent.VK_V);
    robot.keyRelease(KeyEvent.VK_CONTROL);
    Thread.sleep(2000);*/
    Thread.sleep(5000);
    Selenide.switchTo().defaultContent();
  }

  private void waitUntilCanvasLoad(SelenideElement canvas, SelenideElement actionButton) {
    BufferedImage image = canvas.screenshotAsImage();
    actionButton.click();
    canvas.shouldNotBe(new IsCanvasSame(Objects.requireNonNull(image)));
  }

  private void enterEscapeKey() {
    try {
      Robot robot = new Robot();
      Thread.sleep(1000);
      robot.keyPress(KeyEvent.VK_ESCAPE);
      Thread.sleep(1000);
      robot.keyRelease(KeyEvent.VK_ESCAPE);
      Thread.sleep(1000);
    } catch (AWTException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
