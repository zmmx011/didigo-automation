package com.invenia.excel;

import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.converter.ConvertConfig;
import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.poi.sl.usermodel.ObjectMetaData.Application;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableConfigurationProperties({ConvertConfig.class, BatchConfig.class})
@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class ExcelApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ExcelApplication.class).headless(false).run(args);
  }
}
