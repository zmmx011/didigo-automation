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
  public SelenideElement customerMenu = $(By.id("3"));
  public SelenideElement customerGroup = $(By.id("14"));
  public SelenideElement customerInquiry = $(By.id("500373"));
  public SelenideElement customerUpload = $(By.id("502175"));

  /* 구매 관리 */
  public SelenideElement purchaseModule = $(By.cssSelector("li[moduleseq='7100']"));
  public SelenideElement purchaseMasterDataMenu = $(By.id("1"));
  public SelenideElement purchaseUnitPriceGroup = $(By.id("9"));
  public SelenideElement purchaseUnitPrice = $(By.id("501868"));

  /* 영업 관리 */
  public SelenideElement salesModule = $(By.cssSelector("li[moduleseq='7060']"));
  public SelenideElement salesOrderMenu = $(By.id("5"));
  public SelenideElement salesOrderGroup = $(By.id("29"));
  public SelenideElement salesOrderInquiry = $(By.id("500586"));
  public SelenideElement salesOrderUpload = $(By.id("500583"));
}
