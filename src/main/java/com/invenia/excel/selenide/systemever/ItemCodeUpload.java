package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class ItemCodeUpload {

  protected static SelenideElement frame = $(By.id("500259_iframe"));
  protected static SelenideElement file = $(By.id("FrmWDAItemUpload_FileDialog_file"));
  protected static SelenideElement getFileBtn = $(By.id("btnGetFile_btn"));
  protected static SelenideElement getDataBtn = $(By.id("btnGetData_btn"));
  protected static SelenideElement saveBtn = $(By.cssSelector("li[colindex='1']"));
  protected static SelenideElement msgBtnOk = $(By.id("msgBtnOk"));
}
