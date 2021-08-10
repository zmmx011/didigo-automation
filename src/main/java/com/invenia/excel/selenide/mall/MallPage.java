package com.invenia.excel.selenide.mall;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class MallPage {

  public SelenideElement loginId = $(By.name("MBR_ID"));
  public SelenideElement loginPwd = $(By.name("PWD"));
  public SelenideElement loginBtn = $x("//a/img");
  public SelenideElement overrideLink = $(By.id("overridelink"));
  public SelenideElement excelDownloadBtn = $(By.id("DHTMLSuite_menuItem2"));
}


