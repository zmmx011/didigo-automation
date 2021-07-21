package com.invenia.excel;

import com.invenia.excel.converter.dto.Item;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeleniumTests {

  static Logger log = LoggerFactory.getLogger(ExcelConvertTests.class);

  private ChromeDriver chromeDriver;
  private WebDriverWait chromeDriverWait;
  private Actions chromeActions;
  private WebDriver ieDriver;
  private WebDriverWait ieDriverWait;

  public static ExpectedCondition<Boolean> isCanvasBlank(final String canvasId) {
    return new ExpectedCondition<>() {
      @NullableDecl
      @Override
      public Boolean apply(@NullableDecl WebDriver chromeDriver) {
        return (Boolean) ((JavascriptExecutor) chromeDriver).executeScript(
            "const canvas = document.getElementById('" + canvasId + "');" +
                "const context = canvas.getContext('2d');" +
                "const pixelBuffer = new Uint32Array(" +
                "    context.getImageData(0, 0, canvas.width, canvas.height).data.buffer" +
                ");" +
                "return pixelBuffer.some(color => color === 0);");
      }

      @Override
      public String toString() {
        return "canvasId : " + canvasId;
      }
    };
  }

  @Test
  void testOne() throws InterruptedException {
    chromeConfig();
    runCozyDownload();
    try {
      Thread.sleep(5000);
      // 탭 종료
      chromeDriver.close();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      // WebDriver 종료
      chromeDriver.quit();
    }
  }

  @Test
  void testSelenium() throws InterruptedException, IOException, AWTException {

    chromeConfig();
    LocalDate yesterday = LocalDate.now().minusDays(1);
    runSystemEverDownload(yesterday, yesterday);
    newTabAndChange(1);
    runKdErpDownload();
    newTabAndChange(2);
    runCozyDownload();
    ieConfig();
    runMallDownload();

    // 5초 후에 WebDriver 종료
    try {
      Thread.sleep(50000);
      // 탭 종료
      chromeDriver.close();
      ieDriver.close();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      // WebDriver 종료
      chromeDriver.quit();
      ieDriver.quit();
    }
  }

  void chromeConfig() {
    // driver
    Path path = Paths
        .get(System.getProperty("user.dir"), "src/main/resources/driver/chromedriver.exe");

    // WebDriver 경로 설정
    System.setProperty("webdriver.chrome.driver", path.toString());

    // WebDriver 옵션 설정
    HashMap<String, Object> prefs = new HashMap<>();
    prefs.put("download.prompt_for_download", false);
    prefs.put("download.directory_upgrade", true);
    prefs.put("profile.default_content_settings.popups", 0);
    prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
    prefs.put("safebrowsing.enabled", "true");

    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("prefs", prefs);
    options.setExperimentalOption("useAutomationExtension", false);
    options.addArguments("start-maximized");
    options.addArguments("disable-infobars");
    options.addArguments("--safebrowsing-disable-download-protection");
    options.addArguments("safebrowsing-disable-extension-blacklist");
    options.addArguments("--disable-popup-blocking");    // 팝업 무시
    options.addArguments("--disable-default-apps");     // 기본앱 사용안함
    options.setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe");

    // WebDriver 객체 생성
    chromeDriver = new ChromeDriver(options);

    chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    chromeDriverWait = new WebDriverWait(chromeDriver, 10);

    chromeActions = new Actions(chromeDriver);
  }

  void ieConfig() {
    // chromeDriver
    Path path = Paths
        .get(System.getProperty("user.dir"), "src/main/resources/chromeDriver/iechromeDriver.exe");

    // WebDriver 경로 설정
    System.setProperty("webchromeDriver.ie.chromeDriver", path.toString());

    System.setProperty("java.awt.headless", "false");

    ieDriver = new InternetExplorerDriver();

    ieDriverWait = new WebDriverWait(ieDriver, 10);
  }

  void runKdErpDownload() {
    chromeDriver.get("https://mfg.systemevererp.com/");

    // 팝업창 제거
    chromeDriver.executeScript(
        "return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

    // 로그인
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId")))
        .sendKeys("m_itsecurity@inveniacorp.com");
    chromeDriver.findElementById("inputLoginPwd").sendKeys("Invenia_0041");
    chromeDriver.findElementById("btnLogin").click();

    // Loding Area zIndex 낮추기 (방해됨)
    chromeDriver.executeScript(
        "return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

    // 구매 메뉴 선택
    chromeDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7100']")))
        .click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("18"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("501579"))).click();

    // 구매발주품목 조회
    chromeDriverWait
        .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("501579_iframe")));
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtCustName_txt")))
        .sendKeys("디디고");
    //chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("datPODateFr_dat"))).sendKeys("2021-02-01");

    chromeDriver
        .executeScript("return document.getElementById('datPODateFr_dat').value = '2021-02-01';");

    chromeDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']")))
        .click();

    // 엑셀 다운로드
    chromeDriverWait.until(ExpectedConditions.not(isCanvasBlank("SS_cvp_vp")));
    chromeActions.contextClick(chromeDriver.findElementById("SS_btnSheetSetting")).perform();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

    chromeDriver.switchTo().defaultContent();
  }

  void runSystemEverDownload(LocalDate fromDate, LocalDate toDate) {
    // 웹페이지 요청
    chromeDriver.get("https://mfg.systemevererp.com/");

    // 팝업창 제거
    chromeDriver.executeScript(
        "return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

    // 로그인
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId")))
        .sendKeys("d_itsecurity@inveniacorp.com");
    chromeDriver.findElementById("inputLoginPwd").sendKeys("a1234");
    chromeDriver.findElementById("btnLogin").click();

    // 품목 메뉴 선택
    chromeDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7010']")))
        .click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("4"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("19"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("500260"))).click();

    chromeDriverWait
        .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500260_iframe")));

    // 품목 조회
    chromeDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']")))
        .click();

    // 엑셀 다운로드
    chromeDriverWait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
    chromeActions.contextClick(chromeDriver.findElementById("SS1_btnSheetSetting")).perform();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

    chromeDriver.switchTo().defaultContent();

    // Loding Area zIndex 낮추기 (방해됨)
    chromeDriver.executeScript(
        "return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

    chromeDriverWait
        .until(ExpectedConditions.invisibilityOfElementLocated(By.id("divOpenPageLoading")));
    chromeDriverWait
        .until(ExpectedConditions.invisibilityOfElementLocated(By.className("devLoadingArea")));

    // 거래처 메뉴 선택
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("14"))).click();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("500373"))).click();

    // 거래처 조회
    chromeDriverWait
        .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500373_iframe")));
    chromeDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']")))
        .click();

    // 엑셀 다운로드
    chromeDriverWait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
    chromeActions.contextClick(chromeDriver.findElementById("SS1_btnSheetSetting")).perform();
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
  }

  String runCozyDownload() throws InterruptedException {
    chromeDriver.get("http://100.100.16.16:9997");
    // 로그인
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("iEMP_NO")))
        .sendKeys("damu");
    chromeDriver.findElementById("iPASSWORD").sendKeys("inveni@2021");
    chromeDriver.findElementByTagName("button").click();

    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("from")));

    // 엑셀 다운로드
    chromeDriver.executeScript("Chg_Excel();");

    return waitUntilDonwloadCompleted(chromeDriver.getWindowHandle());
  }

  void runMallDownload() throws InterruptedException, AWTException {
    ieDriver.navigate().to("https://admin.didigomall.com:444/");
    // 로그인
    ieDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.name("MBR_ID")))
        .sendKeys("it01");
    ieDriver.findElement(By.name("PWD")).sendKeys("qlalfqjsgh!@34");
    ((JavascriptExecutor) ieDriver).executeScript("login();");

    // 페이지이동 * 주문관리 -> 세금계산서 -> 회원별 정산
    ieDriver.navigate()
        .to("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

    // 엑셀 다운로드
    ieDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("DHTMLSuite_menuItem2")));
    ((JavascriptExecutor) ieDriver).executeScript("Excel();");

    // IE 자동 다운로드 기능이 없어 Robot Class 이용
    Thread.sleep(5000);
    ieDriver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL, "j"));
    Thread.sleep(2000);
    Robot robot = new Robot();
    robot.keyPress(KeyEvent.VK_TAB);
    Thread.sleep(2000);
    robot.keyPress(KeyEvent.VK_ENTER);
    Thread.sleep(2000);
    robot.keyPress(KeyEvent.VK_ESCAPE);
    Thread.sleep(2000);
  }

  private String waitUntilDonwloadCompleted(String windowHanle) throws InterruptedException {
    newTabAndChange(chromeDriver.getWindowHandles().size());
    chromeDriver.get("chrome://downloads/");
    chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("file-link")));
    double percentageProgress = 0;
    while (percentageProgress != 100) {
      percentageProgress = (Long) chromeDriver.executeScript(
          "return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
      System.out.println("Completed Percentage" + percentageProgress);
      Thread.sleep(100);
    }

    String fileName = (String) chromeDriver
        .executeScript("return document.querySelector('downloads-manager')" +
            ".shadowRoot.querySelector('#downloadsList downloads-item')" +
            ".shadowRoot.querySelector('div#content #file-link').text");
    String sourceURL = (String) chromeDriver
        .executeScript("return document.querySelector('downloads-manager')" +
            ".shadowRoot.querySelector('#downloadsList downloads-item')" +
            ".shadowRoot.querySelector('div#content #file-link').href");
    String donwloadedAt = (String) chromeDriver
        .executeScript("return document.querySelector('downloads-manager')" +
            ".shadowRoot.querySelector('#downloadsList downloads-item')" +
            ".shadowRoot.querySelector('div.is-active.focus-row-active #file-icon-wrapper img').src");
    System.out.println("Download deatils");
    System.out.println("File Name :-" + fileName);
    System.out.println("Donwloaded path :- " + donwloadedAt);
    System.out.println("Downloaded from url :- " + sourceURL);
    // print the details
    System.out.println(fileName);
    System.out.println(sourceURL);
    // close the downloads tab2
    chromeDriver.close();
    // switch back to main window
    chromeDriver.switchTo().window(windowHanle);
    return fileName;
  }

  void newTabAndChange(int index) throws InterruptedException {
    Thread.sleep(5000);
    // 빈 탭 생성
    chromeDriver.executeScript("window.open('about:blank','_blank');");

    // 탭 목록 가져오기
    List<String> tabs = new ArrayList<>(chromeDriver.getWindowHandles());

    // 탭 전환
    chromeDriver.switchTo().window(tabs.get(index));
  }

  List<Item> convertHtmlToItem() throws IOException {
    File xls = new File("W:/Downloads/Sales_by_period_2021-06-01_2021-06-14.xls");
    File html = new File("W:/Downloads/Sales_by_period_2021-06-01_2021-06-14.html");

    if (!html.exists()) {
      if (!xls.renameTo(html)) {
        throw new IOException("failed to rename " + xls + " to " + html);
      }
    }

    Document doc = Jsoup.parse(html, "UTF-8");
    List<Item> items = new ArrayList<>();
    doc.select("tbody").select("tr")
        .stream()
        .filter(y -> !"".equals(y.children().get(0).text()))
        .forEach(x -> {
          Elements elements = x.children();
          Item item = new Item();
          item.setIdxNo(elements.get(0).text());
          item.setOrderDate(elements.get(2).text());
          item.setItemNo(elements.get(4).text());
          item.setItemName(elements.get(4).text());
          item.setQty(elements.get(6).text());
          item.setPrice(elements.get(7).text());
          items.add(item);
        });

    return items;
  }

}
