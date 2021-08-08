package com.invenia.excel.selenide.canvas;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

@Slf4j
public class IsCanvasLengthChanged extends Condition {

  @Nonnull
  private final Long orgLength;

  public IsCanvasLengthChanged(Long length) {
    super("is canvas length changed");
    this.orgLength = length;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    String canvasId = element.getAttribute("id");
    Long length = CanvasUtils.getCanvasLength(element);
    log.debug("{} Canvas Length, Original : {} Current : {}", canvasId, orgLength, length );
    return !Objects.equals(orgLength, length);
  }
}
