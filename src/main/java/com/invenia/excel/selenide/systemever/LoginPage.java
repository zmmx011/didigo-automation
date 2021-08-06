package com.invenia.excel.selenide.systemever;

import static com.codeborne.selenide.Selenide.$;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

public class LoginPage {

  protected SelenideElement loginId = $(By.id("txtLoginId"));
  protected SelenideElement loginPwd = $(By.id("inputLoginPwd"));
  protected SelenideElement loginBtn = $(By.id("btnLogin"));
}
