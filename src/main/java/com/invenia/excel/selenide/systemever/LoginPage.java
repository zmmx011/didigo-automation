package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class LoginPage {

  public SelenideElement loginId = $(By.id("txtLoginId"));
  public SelenideElement loginPwd = $(By.id("inputLoginPwd"));
  public SelenideElement loginBtn = $(By.id("btnLogin"));
}
