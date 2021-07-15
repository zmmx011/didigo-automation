package com.invenia.excel.selenium;

import com.invenia.excel.converter.ConvertConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
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
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class Automation {

	private ChromeDriver driver;
	private WebDriverWait wait;
	private Actions actions;

	private final ConvertConfig config;

	public void setup() {
		Path path = Paths.get("C:/excel/driver/chromedriver.exe");

		// WebDriver 경로 설정
		System.setProperty("webdriver.chrome.driver", path.toString());

		System.setProperty("java.awt.headless", "false");

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
		driver = new ChromeDriver(options);

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		wait = new WebDriverWait(driver, 30);

		actions = new Actions(driver);
	}

	public void runCozyDownload(LocalDate fromDate, LocalDate toDate, String url) {
		driver.get(url);
		// 로그인
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iEMP_NO"))).sendKeys("damu");
		driver.findElementById("iPASSWORD").sendKeys("inveni@2021");
		driver.findElementByTagName("button").click();

		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("from")));

		driver.executeScript("return document.getElementById('from').value = '" +
				fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "';");
		driver.executeScript("return document.getElementById('to').value = '" +
				toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "';");

		// 엑셀 다운로드
		driver.executeScript("Chg_Excel();");

		log.info("Cozy 다운로드 완료");
	}

	public void runMallDownload(LocalDate fromDate, LocalDate toDate, String url) throws InterruptedException {
		// driver
		Path path = Paths.get("C:/excel/driver/iedriver.exe");

		// WebDriver 경로 설정
		System.setProperty("webdriver.ie.driver", path.toString());

		System.setProperty("java.awt.headless", "false");

		WebDriver ieDriver = new InternetExplorerDriver();
		WebDriverWait ieWait = new WebDriverWait(ieDriver, 30);
		Thread.sleep(2000);
		ieDriver.navigate().to(url);
		// 로그인
		ieWait.until(ExpectedConditions.presenceOfElementLocated(By.name("MBR_ID"))).sendKeys("it01");
		ieDriver.findElement(By.name("PWD")).sendKeys("qlalfqjsgh!@34");
		((JavascriptExecutor) ieDriver).executeScript("login();");

		Thread.sleep(5000);

		// 페이지이동 * 주문관리 -> 세금계산서 -> 회원별 정산
		ieDriver.navigate().to("https://admin.didigomall.com:444/simpleCommand.do?MNU_ID=086050&PGM_ID=ord003");

		// 기간 설정
		ieWait.until(ExpectedConditions.presenceOfElementLocated(By.id("contractStartDate_con")));
		((JavascriptExecutor) ieDriver).executeScript("return document.getElementById('contractStartDate_con').value = '" +
				fromDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "';");
		((JavascriptExecutor) ieDriver).executeScript("return document.getElementById('contractEndDate_con').value = '" +
				toDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "';");

		// 엑셀 다운로드
		ieWait.until(ExpectedConditions.presenceOfElementLocated(By.id("DHTMLSuite_menuItem2")));
		((JavascriptExecutor) ieDriver).executeScript("Excel();");

		// IE 자동 다운로드 기능이 없어 Robot 이용
		try {
			Thread.sleep(5000);
			ieDriver.findElement(By.tagName("body")).sendKeys(Keys.chord(Keys.CONTROL, "j"));
			Thread.sleep(1000);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_TAB);
			Thread.sleep(1000);
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(1000);
			robot.keyPress(KeyEvent.VK_ESCAPE);
			Thread.sleep(1000);
		} catch (InterruptedException | AWTException e) {
			log.error(e.getMessage(), e);
		}
		Thread.sleep(5000);
		ieDriver.close();
		ieDriver.quit();
		log.info("Mall 다운로드 완료");
	}

	public void runKdErpDownload(LocalDate fromDate, LocalDate toDate, String url) {
		driver.get(url);

		// 팝업창 제거
		driver.executeScript("return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

		// 로그인
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId"))).sendKeys("m_itsecurity@inveniacorp.com");
		driver.findElementById("inputLoginPwd").sendKeys("Invenia_0041");
		driver.findElementById("btnLogin").click();

		// Loding Area zIndex 낮추기 (방해됨)
		driver.executeScript("return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");

		// 구매 메뉴 선택
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7100']"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("18"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("501579"))).click();

		// 구매발주품목 조회
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("501579_iframe")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtCustName_txt"))).sendKeys("디디고");

		// 기간 설정
		driver.executeScript("return document.getElementById('datPODateFr_dat').value = '" +
				fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "';");
		driver.executeScript("return document.getElementById('datPODateTo_dat').value = '" +
				toDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "';");

		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']"))).click();

		// 엑셀 다운로드
		wait.until(ExpectedConditions.not(isCanvasBlank("SS_cvp_vp")));
		actions.contextClick(driver.findElementById("SS_btnSheetSetting")).perform();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

		log.info("KD 다운로드 완료");
	}

	private void runErpLogin(String url) {
		// 웹페이지 요청
		driver.get(url);

		// 팝업창 제거
		driver.executeScript("return document.querySelectorAll('.popupLoginPage').forEach(el => el.remove());");

		// 로그인
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtLoginId"))).sendKeys("d_itsecurity@inveniacorp.com");
		driver.findElementById("inputLoginPwd").sendKeys("a1234");
		driver.findElementById("btnLogin").click();

		// Loding Area zIndex 낮추기 (방해됨)
		driver.executeScript("return document.querySelectorAll('.devLoadingArea').forEach(el => el.style.zIndex = '-100');");
	}

	public void runItemCodeDownload(String url, boolean login) {
		if (login) {
			runErpLogin(url);
		}

		// 품목 메뉴 선택
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7010']"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("4"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("19"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500260"))).click();

		// 품목 조회
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500260_iframe")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='0']"))).click();

		// 엑셀 다운로드
		wait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
		actions.contextClick(driver.findElementById("SS1_btnSheetSetting")).perform();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

		driver.switchTo().defaultContent();
	}

	public void runCustomerDownload(String url, boolean login) {
		if (login) {
			runErpLogin(url);
		}

		// 거래처 메뉴 선택
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7010']"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("14"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500373"))).click();

		// 거래처 조회
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500373_iframe")));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']"))).click();

		// 엑셀 다운로드
		wait.until(ExpectedConditions.not(isCanvasBlank("SS1_cvp_vp")));
		actions.contextClick(driver.findElementById("SS1_btnSheetSetting")).perform();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();

		driver.switchTo().defaultContent();
	}

	public void runItemCodeUpload(String siteName) throws InterruptedException, AWTException {
		if (config.getConvertResult().get(siteName).getItemCodeResult() > 0) {
			Robot robot = new Robot();
			// 전사관리
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7010']"))).click();

			// 품목관리 - 품목등록 - 품목등록업로드
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("4"))).click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("19"))).click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500259"))).click();
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500259_iframe")));

			driver.findElementById("btnGetFile_btn").click();
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ESCAPE);
			Thread.sleep(2000);
			driver.findElementById("FrmWDAItemUpload_FileDialog_file")
					.sendKeys(config.getOutputPath() + siteName + "/" + config.getItemFileName());
			driver.findElementById("btnGetData_btn").click();
			Thread.sleep(5000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']"))).click();
			Thread.sleep(5000);
			driver.switchTo().defaultContent();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("msgBtnOk"))).click();
			log.info(siteName + " 품목 엑셀 업로드 완료");
		}
	}

	public void runContractOrderUpload(String siteName) throws AWTException, InterruptedException {
		log.info("runContractOrderUpload");
		log.info(config.getConvertResult().get(siteName).toString());
		if (config.getConvertResult().get(siteName).getContractOrderResult() > 0) {
			Robot robot = new Robot();

			// 영업관리
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[moduleseq='7060']"))).click();

			// 수주관리 - 수주 - 수주입력업로드
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("5"))).click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("29"))).click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("500583"))).click();
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("500583_iframe")));

			// 수주 구분 (온라인 영업)
			// driver.executeScript("return document.getElementById('txtOrderKindName_ul').value = '" + "8022084'");
			// 부서
			driver.executeScript("return document.getElementById('txtDeptName_txt').value = '" + "영업1팀'");
			driver.findElementById("txtDeptName_txt").click();
			// 담당자
			driver.executeScript("return document.getElementById('txtEmpName_txt').value = '" + "이주현'");
			driver.findElementById("txtEmpName_txt").click();

			driver.findElementById("btnGetFile_btn").click();
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ESCAPE);
			Thread.sleep(2000);
			driver.findElementById("FrmWSLOrderUpload_FileDialog_file")
					.sendKeys(config.getOutputPath() + siteName + "/" + config.getContractOrderFileName());
			driver.findElementById("btnGetData_btn").click();
			Thread.sleep(5000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']"))).click();
			Thread.sleep(5000);
			driver.switchTo().defaultContent();
			log.info(siteName + " 수주 입력 엑셀 업로드 완료");
		}
	}
	/*
	public void runCustomerUpload(String siteName) throws InterruptedException, AWTException {
		Robot robot = new Robot();
		// 거래처관리 - 거래처 등록 - 거래처 등록 업로드
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("3"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("14"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("502175"))).click();
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("502175_iframe")));

		driver.findElementById("btnGetFile_btn").click();
		Thread.sleep(2000);
		robot.keyPress(KeyEvent.VK_ESCAPE);
		Thread.sleep(2000);
		driver.findElementById("FrmWDACustUpload_FileDialog_file")
				.sendKeys(config.getOutputPath() + siteName + "/" + config.getItemFileName());
		driver.findElementById("btnGetData_btn").click();
		Thread.sleep(5000);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[colindex='1']"))).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("msgBtnOk"))).click();
		log.info(siteName + " 거래처 엑셀 업로드 완료");

		driver.switchTo().defaultContent();
	}*/

	public void newTab() {
		// 빈 탭 생성
		driver.executeScript("window.open('about:blank','_blank');");
	}

	public void changeTab(int index) {
		// 탭 목록 가져오기
		List<String> tabs = new ArrayList<>(driver.getWindowHandles());

		// 탭 전환
		driver.switchTo().window(tabs.get(index));
	}

	private ExpectedCondition<Boolean> isCanvasBlank(final String canvasId) {
		return new ExpectedCondition<>() {
			@NullableDecl
			@Override
			public Boolean apply(@NullableDecl WebDriver driver) {
				return (Boolean) ((JavascriptExecutor) driver).executeScript(
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

	public void closeAutomation() {
		driver.close();
		driver.quit();
		log.info("Chrome Driver Close");
	}
}