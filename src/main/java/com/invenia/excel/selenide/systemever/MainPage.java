package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class MainPage {

  public SelenideElement loadingPage = $(By.id("divOpenPageLoading"));
}
