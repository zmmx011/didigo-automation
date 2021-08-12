package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class SalesOrderUploadFrame {

  public SelenideElement frame = $(By.id("500583_iframe"));
  public SelenideElement empName = $(By.id("txtEmpName_txt"));
  public SelenideElement canvas = $(By.id("SS1_cvp_vp"));
  public SelenideElement file = $(By.id("FrmWSLOrderUpload_FileDialog_file"));
  public SelenideElement getFileBtn = $(By.id("btnGetFile_btn"));
  public SelenideElement getDataBtn = $(By.id("btnGetData_btn"));
  public SelenideElement saveBtn = $(By.cssSelector("li[colindex='1']"));
}
