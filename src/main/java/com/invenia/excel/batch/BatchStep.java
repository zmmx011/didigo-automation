package com.invenia.excel.batch;

import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.batch.config.ThrowsBiConsumer;
import com.invenia.excel.converter.ExcelConverter;
import com.invenia.excel.selenide.Automation;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class BatchStep {

  private final ExcelConverter excelConverter;
  private final Automation automation;
  private final StepBuilderFactory stepBuilderFactory;
  private final BatchConfig batchConfig;

  public Step initStep() {
    return stepBuilderFactory
        .get("초기화")
        .tasklet(
            (contribution, chunkContext) -> {
              excelConverter.init();
              automation.runErpLogin(batchConfig.getDidigoWebUrl());
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  public Step itemCodeDownloadStep() {
    return stepBuilderFactory
        .get("품목 다운로드")
        .tasklet(
            (contribution, chunkContext) -> {
              try {
                automation.runItemCodeDownload();
              } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
              }
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  public Step itemPriceDownloadStep() {
    return stepBuilderFactory
        .get("단가 다운로드")
        .tasklet(
            (contribution, chunkContext) -> {
              try {
                automation.runItemPriceDownload();
              } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
              }
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean
  @JobScope
  public Step contractOrderDownloadStep(
      @Value("#{jobParameters['fromDateStr']}") String fromDateStr,
      @Value("#{jobParameters['toDateStr']}") String toDateStr) {
    return stepBuilderFactory
        .get("수주 다운로드")
        .tasklet(
            (contribution, chunkContext) -> {
              LocalDate fromDate = LocalDate.parse(fromDateStr);
              LocalDate toDate = LocalDate.parse(toDateStr);
              try {
                automation.runContractOrderDownload(fromDate, toDate);
              } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
              }
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  public Step customerDownloadStep() {
    return stepBuilderFactory
        .get("거래처 다운로드")
        .tasklet(
            (contribution, chunkContext) -> {
              try {
                automation.runCustomerDownload();
              } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
              }
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  @Bean
  @JobScope
  public Step siteDownloadStep(
      @Value("#{jobParameters['fromDateStr']}") String fromDateStr,
      @Value("#{jobParameters['toDateStr']}") String toDateStr) {
    return stepBuilderFactory
        .get("데이터 수집")
        .tasklet(
            (contribution, chunkContext) -> {
              LocalDate fromDate = LocalDate.parse(fromDateStr);
              LocalDate toDate = LocalDate.parse(toDateStr);
              try {
                if (batchConfig.getRunKd()) {
                }
                if (batchConfig.getRunCozy()) {
                }
                if (batchConfig.getRunMall()) {
                  automation.runMallDownload(fromDate, toDate, batchConfig.getMallWebUrl());
                }
              } catch (Exception e) {
                log.error(e.getLocalizedMessage(), e);
                contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
              }
              return RepeatStatus.FINISHED;
            })
        .build();
  }

  public Step itemConvertStep() {
    return stepBuilderFactory
        .get("품목 단가 변환")
        .tasklet(
            (contribution, chunkContext) ->
                convertRunCheck(ExcelConverter::itemConvert, contribution))
        .build();
  }

  public Step customerConvertStep() {
    return stepBuilderFactory
        .get("거래처 변환")
        .tasklet(
            (contribution, chunkContext) ->
                convertRunCheck(ExcelConverter::customerConvert, contribution))
        .build();
  }

  public Step contractOrderConvertStep() {
    return stepBuilderFactory
        .get("수주 변환")
        .tasklet(
            (contribution, chunkContext) ->
                convertRunCheck(ExcelConverter::contractOrderConvert, contribution))
        .build();
  }

  public Step itemCodeUploadStep() {
    return stepBuilderFactory
        .get("품목 업로드")
        .tasklet(
            (contribution, chunkContext) ->
                automationRunCheck(Automation::runItemCodeUpload, contribution))
        .build();
  }

  public Step itemPriceUploadStep() {
    return stepBuilderFactory
        .get("단가 업로드")
        .tasklet(
            (contribution, chunkContext) ->
                automationRunCheck(Automation::runItemPriceUpload, contribution))
        .build();
  }

  public Step contractOrderUploadStep() {
    return stepBuilderFactory
        .get("수주 업로드")
        .tasklet(
            (contribution, chunkContext) ->
                automationRunCheck(Automation::runContractOrderUpload, contribution))
        .build();
  }

  public Step customerUploadStep() {
    return stepBuilderFactory
        .get("거래처 업로드")
        .tasklet(
            (contribution, chunkContext) ->
                automationRunCheck(Automation::runCustomerUpload, contribution))
        .build();
  }

  private RepeatStatus convertRunCheck(
      ThrowsBiConsumer<ExcelConverter, String> func, StepContribution contribution) {
    try {
      if (batchConfig.getRunKd()) {
        func.accept(excelConverter, "kd");
      }
      if (batchConfig.getRunCozy()) {
        func.accept(excelConverter, "cozy");
      }
      if (batchConfig.getRunMall()) {
        func.accept(excelConverter, "mall");
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
      contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
    }
    return RepeatStatus.FINISHED;
  }

  private RepeatStatus automationRunCheck(
      ThrowsBiConsumer<Automation, String> func, StepContribution contribution) {
    try {
      if (batchConfig.getRunKd()) {
        func.accept(automation, "kd");
      }
      if (batchConfig.getRunCozy()) {
        func.accept(automation, "cozy");
      }
      if (batchConfig.getRunMall()) {
        func.accept(automation, "mall");
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
      contribution.setExitStatus(new ExitStatus("FAILED", e.toString()));
    }
    return RepeatStatus.FINISHED;
  }
}
