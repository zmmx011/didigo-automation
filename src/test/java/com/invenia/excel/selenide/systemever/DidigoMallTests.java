package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.FileDownloadMode;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.files.FileFilter;
import com.codeborne.selenide.files.FileFilters;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.Waiter;
import com.codeborne.selenide.proxy.FileDownloadFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.errorprone.annotations.CheckReturnValue;
import com.invenia.excel.selenide.mall.MallPage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class DidigoMallTests {

  private static final Logger log = LoggerFactory.getLogger(DidigoMallTests.class);

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

  @Test
  @DisplayName("회원 별 정산 다운로드")
  public void mallDownloadTest() throws FileNotFoundException {
    Selenide.open("https://admin.didigomall.com:444/");
    // 로그인
    MallPage mallPage = new MallPage();
    mallPage.loginId.sendKeys("it01");
    mallPage.loginPwd.sendKeys("qlalfqjsgh!@34");
    mallPage.loginBtn.click();

    Selenide.open("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

    FileFilter fileFilter = FileFilters.withExtension("xls");
    String from = "20210801";
    String to = "20210810";

    WebDriver webDriver = getWebDriver();
    String originalWindowHandle = webDriver.getWindowHandle();
    Set<String> windowsBefore = webDriver.getWindowHandles();

    try {
      File downloadFile = executeJSAndInterceptFileByProxyServer(fileFilter, from, to);
      assertTrue(downloadFile.exists());
      log.info("회원 별 정산 다운로드 완료 : " + downloadFile.getAbsolutePath());
    } finally {
      closeArisedWindows(webDriver, originalWindowHandle, windowsBefore);
    }
  }

  public File executeJSAndInterceptFileByProxyServer(FileFilter fileFilter, String fromDate, String toDate)
      throws FileNotFoundException {
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

    SelenideProxyServer proxyServer = getSelenideProxy();
    Waiter waiter = new Waiter();
    long timeout = Configuration.timeout;
    long pollingInterval = Configuration.pollingInterval;

    FileDownloadFilter filter = Objects.requireNonNull(proxyServer).responseFilter("download");
    if (filter == null) {
      throw new IllegalStateException("Cannot download file: download filter is not activated");
    }
    filter.activate();
    try {
      Selenide.executeJavaScript(jsCode, fromDate, toDate);
      waiter.wait(filter, new HasDownloads(fileFilter), timeout, pollingInterval);
      return filter.downloads().firstMatchingFile(fileFilter)
          .orElseThrow(() -> new FileNotFoundException(String.format("Failed to download file in %d ms. %s",
                  timeout, fileFilter.description()).trim()
              )
          ).getFile();
    } finally {
      filter.deactivate();
    }
  }

  @ParametersAreNonnullByDefault
  private static class HasDownloads implements Predicate<FileDownloadFilter> {

    private final FileFilter fileFilter;

    private HasDownloads(FileFilter fileFilter) {
      this.fileFilter = fileFilter;
    }

    @Override
    public boolean test(FileDownloadFilter filter) {
      return !filter.downloads().files(fileFilter).isEmpty();
    }
  }

  private void closeArisedWindows(WebDriver webDriver, String originalWindowHandle, Set<String> windowsBefore) {
    Set<String> newWindows = newWindows(webDriver, windowsBefore);
    if (!newWindows.isEmpty()) {
      closeWindows(webDriver, newWindows);
      webDriver.switchTo().window(originalWindowHandle);
    }
  }

  @CheckReturnValue
  @Nonnull
  private Set<String> newWindows(WebDriver webDriver, Set<String> windowsBefore) {
    Set<String> windowHandles = webDriver.getWindowHandles();

    Set<String> newWindows = new HashSet<>(windowHandles);
    newWindows.removeAll(windowsBefore);
    return newWindows;
  }

  private void closeWindows(WebDriver webDriver, Set<String> windows) {
    log.info("File has been opened in a new window, let's close {} new windows", windows.size());

    for (String newWindow : windows) {
      closeWindow(webDriver, newWindow);
    }
  }

  private void closeWindow(WebDriver webDriver, String window) {
    log.info("  Let's close {}", window);
    try {
      webDriver.switchTo().window(window);
      webDriver.close();
    } catch (NoSuchWindowException windowHasBeenClosedMeanwhile) {
      log.info("  Failed to close {}: {}", window, Cleanup.of.webdriverExceptionMessage(windowHasBeenClosedMeanwhile));
    } catch (Exception e) {
      log.warn("  Failed to close {}", window, e);
    }
  }
}
