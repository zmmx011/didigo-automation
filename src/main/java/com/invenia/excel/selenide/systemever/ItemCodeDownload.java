package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class ItemCodeDownload {

  protected static SelenideElement frame = $(By.id("500260_iframe"));
  protected static SelenideElement inquiryBtn = $(By.cssSelector("li[colindex='0']"));
  protected static SelenideElement canvasId = $(By.id("SS1_cvp_vp"));
  protected static SelenideElement sheetSettingBtn = $(By.id("SS1_btnSheetSetting"));
  protected static SelenideElement excelDownloadBtn = $(By.id("3"));
}
