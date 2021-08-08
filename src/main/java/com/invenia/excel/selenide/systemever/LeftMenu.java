package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class LeftMenu {

  /* 전사 관리 */
  public SelenideElement corporateModule = $(By.cssSelector("li[moduleseq='7010']"));
  public SelenideElement itemMenu = $(By.id("4"));
  public SelenideElement itemGroup = $(By.id("19"));
  public SelenideElement item = $(By.id("500260"));
  public SelenideElement itemUpload = $(By.id("500259"));

  /* 구매 관리 */
  public SelenideElement purchaseModule = $(By.cssSelector("li[moduleseq='7100']"));
  public SelenideElement purchaseMasterDataMenu = $(By.id("1"));
  public SelenideElement purchaseUnitPriceGroup = $(By.id("9"));
  public SelenideElement purchaseUnitPrice = $(By.id("501868"));
}
