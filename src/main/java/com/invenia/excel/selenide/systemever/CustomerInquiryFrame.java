package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class CustomerInquiryFrame {

  public SelenideElement frame = $(By.id("500373_iframe"));
  public SelenideElement inquiryBtn = $(By.cssSelector("li[colindex='1']"));
  public SelenideElement canvas = $(By.id("SS1_cvp_vp"));
  public SelenideElement sheetSettingBtn = $(By.id("SS1_btnSheetSetting"));
  public SelenideElement excelDownloadBtn = $(By.id("3"));
}
