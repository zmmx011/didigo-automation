package com.invenia.excel;

import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.converter.ConvertConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties({ConvertConfig.class, BatchConfig.class})
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class ExcelApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ExcelApplication.class).headless(false).run(args);
  }
}
