package com.invenia.excel.selenide.systemever;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class SelenideTests {

  LeftMenu leftMenu = new LeftMenu();

  @BeforeAll
  public static void setUpClass() {
    Configuration.fastSetValue = true;
    Configuration.timeout = 10000;
    Configuration.headless = true;
  }

  @Test
  public void login() {
    Selenide.open("https://mfg.systemevererp.com/");
    LoginPage loginPage = new LoginPage();
    Selenide.executeJavaScript("document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");
    loginPage.loginId.val("d_itsecurity@inveniacorp.com");
    loginPage.loginPwd.val("inveni@2021");
    loginPage.loginBtn.click();
  }

  @Test
  public void itemCodeDownload() throws Exception {
    login();
    // 메뉴 선택
    leftMenu.corporateModule.click();
    leftMenu.itemMenu.click();
    leftMenu.itemGroup.click();
    leftMenu.itemInquiry.click();
    waitLoading();

    // 품목 다운로드
    Selenide.switchTo().frame(ItemCodeDownload.frame);
    ItemCodeDownload.inquiryBtn.click();
  }

  @Test
  public void itemCodeUpload() throws Exception {
    login();
    // 메뉴 선택
    leftMenu.corporateModule.click();
    leftMenu.itemMenu.click();
    leftMenu.itemGroup.click();
    leftMenu.itemUploadProgram.click();
    waitLoading();
    // 품목 업로드
    Selenide.switchTo().frame(ItemCodeUpload.frame);
    ItemCodeUpload.getFileBtn.click();
    ItemCodeUpload.file.uploadFile(new ClassPathResource("item-upload/RptWDAItemUpload.xlsx").getFile());
    ItemCodeUpload.getDataBtn.click();
    waitLoading();
    ItemCodeUpload.saveBtn.click();
    waitLoading();
    Selenide.switchTo().defaultContent();
    assertTrue(ItemCodeUpload.msgBtnOk.shouldBe(Condition.visible).isDisplayed());
  }

  private void waitLoading() throws InterruptedException {
    Thread.sleep(1000);
    MainPage.loadingPage.shouldNot(Condition.visible);
  }

  private ExpectedCondition<Boolean> isCanvasBlank(final String canvasId) {
    return new ExpectedCondition<>() {
      @Override
      public Boolean apply(WebDriver driver) {
        return (Boolean)
            ((JavascriptExecutor) driver)
                .executeScript(
                    "const canvas = document.getElementById('"
                        + canvasId
                        + "');"
                        + "const context = canvas.getContext('2d');"
                        + "const pixelBuffer = new Uint32Array("
                        + "    context.getImageData(0, 0, canvas.width, canvas.height).data.buffer"
                        + ");"
                        + "return pixelBuffer.some(color => color === 0);");
      }

      @Override
      public String toString() {
        return "canvasId : " + canvasId;
      }
    };
  }
}

