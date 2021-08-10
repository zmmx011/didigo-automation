package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class SalesOrderInquiryFrame {

  public SelenideElement frame = $(By.id("500586_iframe"));
  public SelenideElement inquiryBtn = $(By.cssSelector("li[colindex='0']"));
  public SelenideElement canvas = $(By.id("SS1_cvp_vp"));
  public SelenideElement sheetSettingBtn = $(By.id("SS1_btnSheetSetting"));
  public SelenideElement excelDownloadBtn = $(By.id("3"));
  public SelenideElement bizUnit = $(By.id("txtBizUnitName_ul"));
  public SelenideElement salesOrderType = $(By.id("txtOrderKindName_ul"));
  public SelenideElement salesOrderDateFrom = $(By.id("datOrderDateFr_dat"));
  public SelenideElement deliveryDateFrom = $(By.id("datDVDateFr_dat"));
  public SelenideElement deliveryDateTo = $(By.id("datDVDateTo_dat"));
}
