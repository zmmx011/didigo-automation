package com.invenia.excel.batch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("batch.config")
public class BatchConfig {

  private String didigoWebUrl;
  private String cozyWebUrl;
  private String kdWebUrl;
  private String mallWebUrl;

  private Boolean runCozy;
  private Boolean runKd;
  private Boolean runMall;

  private int tabIndex;

  public int getTabIndexIncrease() {
    return ++tabIndex;
  }
}
