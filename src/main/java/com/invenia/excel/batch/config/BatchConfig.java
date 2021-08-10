package com.invenia.excel.batch.config;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
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

  public static JobParameters getJobParameters(String fromDate, String toDate) {
    return new JobParametersBuilder()
        .addString("dateTime", LocalDateTime.now().toString())
        .addString("fromDateStr", fromDate)
        .addString("toDateStr", toDate)
        .toJobParameters();
  }
}
