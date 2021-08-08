package com.invenia.excel.selenide.systemever;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.files.FileFilters;
import com.invenia.excel.selenide.canvas.CanvasUtils;
import com.invenia.excel.selenide.canvas.IsCanvasLengthChanged;
import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class SelenideTests {

  MainPage main = new MainPage();
  LeftMenu menu = new LeftMenu();

  @BeforeAll
  public static void setup() {
    Configuration.fastSetValue = true;
    Configuration.timeout = 10000;
    Configuration.headless = true;
    Configuration.proxyEnabled = true;
    Configuration.fileDownload = FileDownloadMode.PROXY;
  }

  @AfterAll
  public static void close() {
    Selenide.closeWebDriver();
  }

  @BeforeEach
  public void login() {
    LoginPage page = new LoginPage();
    Selenide.open("https://mfg.systemevererp.com/");
    Selenide.executeJavaScript("document.querySelectorAll('.popuploginPage').forEach(el => el.remove());");
    page.loginId.val("d_itsecurity@inveniacorp.com");
    page.loginPwd.val("inveni@2021");
    page.loginBtn.click();
  }

  @Test
  @DisplayName("품목 다운로드")
  public void itemCodeDownloadTest() throws Exception {
    // 전사 관리 - 품목 관리 - 품목 등록 - 품목 조회
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.item.click();
    main.waitLoading();

    // 품목 다운로드
    ItemCodeDownloadFrame frame = new ItemCodeDownloadFrame();
    Selenide.switchTo().frame(frame.frame);
    SelenideElement canvas = frame.canvas;
    Long canvasLength = CanvasUtils.getCanvasLength(canvas);
    frame.inquiryBtn.click();
    canvas.shouldBe(new IsCanvasLengthChanged(canvasLength));
    frame.sheetSettingBtn.contextClick();
    File downloadFile = frame.excelDownloadBtn.download(FileFilters.withExtension("xlsx"));
    Selenide.switchTo().defaultContent();
    assertTrue(downloadFile.exists());
  }

  @Test
  @DisplayName("구매 단가 다운로드")
  public void itemPriceDownloadTest() {
    // 구매 관리 - 구매 기준 정보 - 구매 단가 - 구매 단가 등록
  }

  @Test
  @DisplayName("품목 업로드")
  public void itemCodeUploadTest() throws Exception {
    // 전사 관리 - 품목 관리 - 품목 등록 - 품목 등록 업로드
    menu.corporateModule.click();
    menu.itemMenu.click();
    menu.itemGroup.click();
    menu.itemUpload.click();
    main.waitLoading();
    // 품목 업로드
    ItemCodeUploadFrame frame = new ItemCodeUploadFrame();
    Selenide.switchTo().frame(frame.frame);
    frame.getFileBtn.click();
    frame.file.uploadFile(new ClassPathResource("item-upload/RptWDAItemUpload.xlsx").getFile());
    Long canvasLength = CanvasUtils.getCanvasLength(frame.canvas);
    frame.getDataBtn.click();
    frame.canvas.shouldBe(new IsCanvasLengthChanged(canvasLength));
    frame.saveBtn.click();
    main.waitLoading();
    Selenide.switchTo().defaultContent();
    assertTrue(frame.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
  }
}

