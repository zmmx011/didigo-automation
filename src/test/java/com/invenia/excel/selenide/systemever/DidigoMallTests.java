package com.invenia.excel.selenide.systemever;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.invenia.excel.selenide.mall.MallPage;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class DidigoMallTests {

  @BeforeAll
  public static void setup() {
    Configuration.browser = WebDriverRunner.INTERNET_EXPLORER;
    //System.setProperty("java.awt.headless", "false");
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

  @Test
  @DisplayName("회원 별 정산 다운로드")
  public void itemCodeDownloadTest() throws InterruptedException, FileNotFoundException {
    Selenide.open("https://admin.didigomall.com:444/");
    //Selenide.open("javascript:document.getElementById('overridelink').click()");
    // 로그인
    MallPage mallPage = new MallPage();
    mallPage.loginId.sendKeys("it01");
    mallPage.loginPwd.sendKeys("qlalfqjsgh!@34");
    mallPage.loginBtn.click();

    Selenide.open("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

    mallPage.excelDownloadBtn.download();

    //Thread.sleep(5000);
  }
}
