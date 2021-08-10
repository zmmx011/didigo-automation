package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class PurchaseUnitPriceFrame {

  public SelenideElement frame = $(By.id("501868_iframe"));
  public SelenideElement newBtn = $(By.cssSelector("li[colindex='0']"));
  public SelenideElement inquiryBtn = $(By.cssSelector("li[colindex='1']"));
  public SelenideElement saveBtn = $(By.cssSelector("li[colindex='2']"));
  public SelenideElement canvas = $(By.id("SS_cvp_vp"));
  public SelenideElement sheetSettingBtn = $(By.id("SS_btnSheetSetting"));
  public SelenideElement excelDownloadBtn = $(By.id("3"));
}
