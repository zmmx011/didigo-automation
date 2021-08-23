package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class LeftMenu {

  public SelenideElement corporateModule = $(By.cssSelector("li[moduleseq='7010']")); // 전사 관리
  public SelenideElement itemMenu = $(By.id("4")); // 품목 관리
  public SelenideElement itemGroup = $(By.id("19")); // 품목 등록
  public SelenideElement item = $(By.id("500260")); // 품목 조회
  public SelenideElement itemUpload = $(By.id("500259")); // 품목 등록 업로드
  public SelenideElement customerMenu = $(By.id("3")); // 거래처 관리
  public SelenideElement customerGroup = $(By.id("14")); // 거래처 등록
  public SelenideElement customerInquiry = $(By.id("500373")); // 거래처 조회
  public SelenideElement customerUpload = $(By.id("502175")); // 거래처 등록 업로드

  public SelenideElement purchaseModule = $(By.cssSelector("li[moduleseq='7100']")); // 구매 관리
  public SelenideElement purchaseMasterDataMenu = $(By.id("1")); // 구매 기준 정보
  public SelenideElement purchaseUnitPriceGroup = $(By.id("9")); // 구매 단가
  public SelenideElement purchaseUnitPrice = $(By.id("501868")); // 구매 단가 등록
  public SelenideElement purchaseActivityMngMenu = $(By.id("3")); // 구매 활동 관리
  public SelenideElement purchaseOrderGroup = $(By.id("18")); // 구매 발주
  public SelenideElement purchaseOrderItemInquiry = $(By.id("501579")); // 구매 발주 품목 조회

  public SelenideElement salesModule = $(By.cssSelector("li[moduleseq='7060']")); // 영업 관리
  public SelenideElement salesOrderMenu = $(By.id("5")); // 수주 관리
  public SelenideElement salesOrderGroup = $(By.id("29")); // 수주
  public SelenideElement salesOrderInquiry = $(By.id("500586")); // 수주 조회
  public SelenideElement salesOrderUpload = $(By.id("500583")); // 수주 입력 업로드
}
