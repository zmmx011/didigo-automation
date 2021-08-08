package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class MainPage {

  public static SelenideElement loadingPage = $(By.id("divOpenPageLoading"));

  public void waitLoading() throws InterruptedException {
    Thread.sleep(1000);
    MainPage.loadingPage.shouldNot(Condition.visible);
  }
}
