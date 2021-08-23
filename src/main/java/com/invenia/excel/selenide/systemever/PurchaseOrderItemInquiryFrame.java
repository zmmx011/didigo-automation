package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class PurchaseOrderItemInquiryFrame {

  public SelenideElement frame = $(By.id("501579_iframe"));
  public SelenideElement customer = $(By.id("txtCustName_txt"));
  public SelenideElement purchaseOrderDateFrom = $(By.id("datPODateFr_dat"));
  public SelenideElement purchaseOrderDateTo = $(By.id("datPODateTo_dat"));
  public SelenideElement inquiryBtn = $(By.cssSelector("li[colindex='0']"));
  public SelenideElement canvas = $(By.id("SS_cvp_vp"));
  public SelenideElement sheetSettingBtn = $(By.id("SS_btnSheetSetting"));
  public SelenideElement excelDownloadBtn = $(By.id("3"));
}
