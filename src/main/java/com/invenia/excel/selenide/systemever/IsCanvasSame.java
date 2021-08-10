package com.invenia.excel.selenide.systemever;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import java.awt.image.BufferedImage;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

@ParametersAreNonnullByDefault
@Slf4j
public class IsCanvasSame extends Condition {

  private final BufferedImage image;

  public IsCanvasSame(BufferedImage image) {
    super("is image same");
    this.image = image;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    BufferedImage currentImage = ScreenShotLaboratory.getInstance().takeScreenshotAsImage(driver, element);
    return compareImages(this.image, Objects.requireNonNull(currentImage));
  }

  private boolean compareImages(BufferedImage firstImage, BufferedImage secondImage) {
    if (firstImage.getWidth() != secondImage.getWidth() || firstImage.getHeight() != secondImage.getHeight()) {
      return false;
    }

    int width = firstImage.getWidth();
    int height = firstImage.getHeight();
    int ignorePixelY = 50; // canvas 조작부 그림자 문제 때문에 무시 영역 지정
    int count = 0;
    for (int y = ignorePixelY; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (firstImage.getRGB(x, y) != secondImage.getRGB(x, y)) {
          count++;
        }
      }
    }
    log.debug("Image Same Check Different Pixel Count : {}", count);
    return count == 0 || count > 100000;
  }
}
