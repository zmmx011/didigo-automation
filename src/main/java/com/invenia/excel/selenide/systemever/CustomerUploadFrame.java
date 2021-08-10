package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class CustomerUploadFrame {

  public SelenideElement frame = $(By.id("502175_iframe"));
  public SelenideElement canvas = $(By.id("SS1_cvp_vp"));
  public SelenideElement file = $(By.id("FrmWDACustUpload_FileDialog_file"));
  public SelenideElement getFileBtn = $(By.id("btnGetFile_btn"));
  public SelenideElement getDataBtn = $(By.id("btnGetData_btn"));
  public SelenideElement saveBtn = $(By.cssSelector("li[colindex='1']"));
  public SelenideElement msgBtnOk = $(By.id("msgBtnOk"));
}
