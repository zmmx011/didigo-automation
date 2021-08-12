package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

@Slf4j
public class MainPage {

  public SelenideElement loadingPage = $(By.id("divOpenPageLoading"));
  public SelenideElement tabCloseButton = $(By.id("btnCloseOpenPage"));
  public SelenideElement msgBtnOk = $(By.id("msgBtnOk"));
}
