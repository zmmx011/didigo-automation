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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class SeleniumTests {

  private ChromeDriver driver;
  private WebDriverWait wait;
  private Actions actions;
  private WebDriver ieDriver;
  private WebDriverWait ieDriverWait;

  public static ExpectedCondition<Boolean> isCanvasBlank(final String canvasId) {
    return new ExpectedCondition<>() {
      @Override
      public Boolean apply(WebDriver chromeDriver) {
        return (Boolean)
            ((JavascriptExecutor) chromeDriver)
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

  @Test
  void testClipboard() throws IOException, AWTException, InterruptedException {
    System.setProperty("java.awt.headless", "false");
    Thread.sleep(2000);
    Robot robot = new Robot();

    chromeConfig();

    driver.get("https://mfg.systemevererp.com/");

    // ????????? ??????
    driver.executeScript(
        "return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

    // ?????????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId")))
        .sendKeys("d_itsecurity@inveniacorp.com");
    driver.findElementById("inputLoginPwd").sendKeys("a1234");
    driver.findElementById("btnLogin").click();

    // Loding Area zIndex ????????? (?????????)
    driver.executeScript(
        "return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

    // ?????? ?????? ??????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7100']")))
        .click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("1"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("9"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("501868"))).click();

    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("501868_iframe")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("SS_cvp_vp"))).click();
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
    Thread.sleep(2000);
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='2']")))
        .click();

    driver.switchTo().defaultContent();
    Thread.sleep(50000);
    if (driver != null) {
      driver.close();
      driver.quit();
    }
  }

  void chromeConfig() {
    // driver
    Path path =
        Paths.get(
            System.getProperty("user.dir"), "src/main/resources/deploy/driver/chromedriver.exe");

    // WebDriver ?????? ??????
    System.setProperty("webdriver.chrome.driver", path.toString());

    // WebDriver ?????? ??????
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
    options.addArguments("--disable-popup-blocking"); // ?????? ??????
    options.addArguments("--disable-default-apps"); // ????????? ????????????
    options.setBinary("C:/Program Files/Google/Chrome/Application/chrome.exe");

    // WebDriver ?????? ??????
    driver = new ChromeDriver(options);

    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    wait = new WebDriverWait(driver, 10);

    actions = new Actions(driver);
  }

  void ieConfig() {
    // chromeDriver
    Path path =
        Paths.get(
            System.getProperty("user.dir"), "src/main/resources/chromeDriver/iechromeDriver.exe");

    // WebDriver ?????? ??????
    System.setProperty("webchromeDriver.ie.chromeDriver", path.toString());

    System.setProperty("java.awt.headless", "false");

    ieDriver = new InternetExplorerDriver();

    ieDriverWait = new WebDriverWait(ieDriver, 10);
  }

  void runKdErpDownload() {
    driver.get("https://mfg.systemevererp.com/");

    // ????????? ??????
    driver.executeScript(
        "return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

    // ?????????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId")))
        .sendKeys("m_itsecurity@inveniacorp.com");
    driver.findElementById("inputLoginPwd").sendKeys("Invenia_0041");
    driver.findElementById("btnLogin").click();

    // Loding Area zIndex ????????? (?????????)
    driver.executeScript(
        "return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

    // ?????? ?????? ??????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7100']")))
        .click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("18"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("501579"))).click();

    // ?????????????????? ??????
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("501579_iframe")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtCustName_txt")))
        .sendKeys("?????????");
    // chromeDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("datPODateFr_dat"))).sendKeys("2021-02-01");

    driver.executeScript("return document.getElementById('datPODateFr_dat').value = '2021-02-01';");

    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']")))
        .click();

    // ?????? ????????????
    wait.until(ExpectedConditions.not(isCanvasBlank("SS_cvp_vp")));
    actions.contextClick(driver.findElementById("SS_btnSheetSetting")).perform();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

    driver.switchTo().defaultContent();
  }

  void runSystemEverDownload(LocalDate fromDate, LocalDate toDate) {
    // ???????????? ??????
    driver.get("https://mfg.systemevererp.com/");

    // ????????? ??????
    driver.executeScript(
        "return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

    // ?????????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId")))
        .sendKeys("d_itsecurity@inveniacorp.com");
    driver.findElementById("inputLoginPwd").sendKeys("a1234");
    driver.findElementById("btnLogin").click();

    // ?????? ?????? ??????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7010']")))
        .click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("4"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("19"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500260"))).click();

    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500260_iframe")));

    // ?????? ??????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']")))
        .click();

    // ?????? ????????????
    wait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
    actions.contextClick(driver.findElementById("SS1_btnSheetSetting")).perform();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

    driver.switchTo().defaultContent();

    // Loding Area zIndex ????????? (?????????)
    driver.executeScript(
        "return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divOpenPageLoading")));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("devLoadingArea")));

    // ????????? ?????? ??????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("14"))).click();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500373"))).click();

    // ????????? ??????
    wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500373_iframe")));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']")))
        .click();

    // ?????? ????????????
    wait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
    actions.contextClick(driver.findElementById("SS1_btnSheetSetting")).perform();
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
  }

  String runCozyDownload() throws InterruptedException {
    driver.get("http://100.100.16.16:9997");
    // ?????????
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iEMP_NO"))).sendKeys("damu");
    driver.findElementById("iPASSWORD").sendKeys("inveni@2021");
    driver.findElementByTagName("button").click();

    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("from")));

    // ?????? ????????????
    driver.executeScript("Chg_Excel();");

    return waitUntilDonwloadCompleted(driver.getWindowHandle());
  }

  void runMallDownload() throws InterruptedException, AWTException {
    ieDriver.navigate().to("https://admin.didigomall.com:444/");
    // ?????????
    ieDriverWait
        .until(ExpectedConditions.presenceOfElementLocated(By.name("MBR_ID")))
        .sendKeys("it01");
    ieDriver.findElement(By.name("PWD")).sendKeys("qlalfqjsgh!@34");
    ((JavascriptExecutor) ieDriver).executeScript("login();");

    // ??????????????? * ???????????? -> ??????????????? -> ????????? ??????
    ieDriver
        .navigate()
        .to("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

    // ?????? ????????????
    ieDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("DHTMLSuite_menuItem2")));
    ((JavascriptExecutor) ieDriver).executeScript("Excel();");

    // IE ?????? ???????????? ????????? ?????? Robot Class ??????
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
    newTabAndChange(driver.getWindowHandles().size());
    driver.get("chrome://downloads/");
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("file-link")));
    double percentageProgress = 0;
    while (percentageProgress != 100) {
      percentageProgress =
          (Long)
              driver.executeScript(
                  "return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('#progress').value");
      System.out.println("Completed Percentage" + percentageProgress);
      Thread.sleep(100);
    }

    String fileName =
        (String)
            driver.executeScript(
                "return document.querySelector('downloads-manager')"
                    + ".shadowRoot.querySelector('#downloadsList downloads-item')"
                    + ".shadowRoot.querySelector('div#content #file-link').text");
    String sourceURL =
        (String)
            driver.executeScript(
                "return document.querySelector('downloads-manager')"
                    + ".shadowRoot.querySelector('#downloadsList downloads-item')"
                    + ".shadowRoot.querySelector('div#content #file-link').href");
    String donwloadedAt =
        (String)
            driver.executeScript(
                "return document.querySelector('downloads-manager')"
                    + ".shadowRoot.querySelector('#downloadsList downloads-item')"
                    + ".shadowRoot.querySelector('div.is-active.focus-row-active #file-icon-wrapper img').src");
    System.out.println("Download deatils");
    System.out.println("File Name :-" + fileName);
    System.out.println("Donwloaded path :- " + donwloadedAt);
    System.out.println("Downloaded from url :- " + sourceURL);
    // print the details
    System.out.println(fileName);
    System.out.println(sourceURL);
    // close the downloads tab2
    driver.close();
    // switch back to main window
    driver.switchTo().window(windowHanle);
    return fileName;
  }

  void newTabAndChange(int index) throws InterruptedException {
    Thread.sleep(5000);
    // ??? ??? ??????
    driver.executeScript("window.open('about:blank','_blank');");

    // ??? ?????? ????????????
    List<String> tabs = new ArrayList<>(driver.getWindowHandles());

    // ??? ??????
    driver.switchTo().window(tabs.get(index));
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
    doc.select("tbody").select("tr").stream()
        .filter(y -> !"".equals(y.children().get(0).text()))
        .forEach(
            x -> {
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
