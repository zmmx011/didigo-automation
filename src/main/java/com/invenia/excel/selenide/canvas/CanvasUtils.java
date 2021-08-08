package com.invenia.excel.selenide.canvas;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;

public class CanvasUtils {

  public static Long getCanvasLength(WebElement element) {
    return Selenide.executeJavaScript(
        "return new Uint32Array("
            + "arguments[0].getContext('2d').getImageData(0, 0, arguments[0].width, arguments[0].height).data.buffer"
            + ").length;", element
    );
  }
}
