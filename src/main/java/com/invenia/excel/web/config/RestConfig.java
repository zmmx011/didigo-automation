package com.invenia.excel.web.config;

import com.invenia.excel.web.entity.BatchJobExecution;
import com.invenia.excel.web.entity.BatchStepExecution;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
    RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);
    config.exposeIdsFor(BatchJobExecution.class);
    config.exposeIdsFor(BatchStepExecution.class);
  }
}
