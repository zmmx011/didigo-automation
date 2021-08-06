package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class LeftMenu {

  protected SelenideElement corporateModule = $(By.cssSelector("li[moduleseq='7010']"));
  protected SelenideElement itemMenu = $(By.id("4"));
  protected SelenideElement itemGroup = $(By.id("19"));
  protected SelenideElement itemInquiry = $(By.id("500260"));
  protected SelenideElement itemUploadProgram = $(By.id("500259"));
}
