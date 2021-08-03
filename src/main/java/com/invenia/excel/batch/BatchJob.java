package com.invenia.excel.batch;

import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.common.AnsiColorEscapeSequence;
import com.invenia.excel.converter.ExcelConverter;
import com.invenia.excel.selenium.Automation;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class BatchJob {

  private final JobBuilderFactory jobBuilderFactory;
  private final ExcelConverter excelConverter;
  private final Automation automation;
  private final BatchConfig batchConfig;
  private final BatchStep step;
  private final BatchMail mail;

  @Bean // 거래처 등록
  public Job customerJob() {
    return jobBuilderFactory
        .get("거래처 등록")
        .incrementer(new RunIdIncrementer())
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep()) // 초기화
        .next(step.customerDownloadStep()) // 거래처 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.siteDownloadStep(null, null)) // 데이터 수집
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerConvertStep()) // 거래처 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerUploadStep()) // 거래처 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .end()
        .build();
  }

  @Bean // 품목 및 단가 등록
  public Job itemCodeJob() {
    return jobBuilderFactory
        .get("품목 단가 등록")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep()) // 초기화
        .next(step.itemCodeDownloadStep()) // 품목 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemPriceDownloadStep()) // 단가 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerDownloadStep()) // 거래처 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.siteDownloadStep(null, null)) // 데이터 수집
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemConvertStep()) // 품목 단가 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerConvertStep()) // 거래처 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemCodeUploadStep()) // 품목 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerUploadStep()) // 거래처 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemPriceUploadStep()) // 단가 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .end()
        .build();
  }

  @Bean // 수주 등록
  public Job contractOrderJob() {
    return jobBuilderFactory
        .get("수주 등록")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep()) // 초기화
        .next(step.itemCodeDownloadStep()) // 품목 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemPriceDownloadStep()) // 단가 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerDownloadStep()) // 거래처 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.contractOrderDownloadStep(null, null)) // 수주 다운로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.siteDownloadStep(null, null)) // 데이터 수집
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerConvertStep()) // 거래처 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemConvertStep()) // 품목 단가 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.contractOrderConvertStep()) // 수주 변환
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemCodeUploadStep()) // 품목 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.customerUploadStep()) // 거래처 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.itemPriceUploadStep()) // 단가 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .next(step.contractOrderUploadStep()) // 수주 업로드
        .on(ExitStatus.FAILED.getExitCode())
        .fail()
        .end()
        .build();
  }

  private class ConvertJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
      log.info("runSendMail : " + batchConfig.getRunSendMail());
      log.info(
          "runCozy : "
              + batchConfig.getRunCozy()
              + " runKd : "
              + batchConfig.getRunKd()
              + " runMall : "
              + batchConfig.getRunMall());
      try {
        automation.setup();
      } catch (IOException e) {
        log.error(e.getLocalizedMessage(), e);
      }
      log.info(jobExecution.getJobInstance().getJobName() + " started");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
      // Automation Quit
      automation.quitAutomation();
      log.info(AnsiColorEscapeSequence.MAGENTA.es() + jobExecution.getExitStatus());
      // 실패시 메일 발송
      if (jobExecution.getExitStatus().equals(ExitStatus.FAILED)) {
        mail.sendJobFailureMail(jobExecution);
      }
      try {
        excelConverter.fileBackup(jobExecution.getId());
        excelConverter.clearOutputPath();
      } catch (IOException e) {
        log.error(e.getLocalizedMessage(), e);
      } finally {
        log.info(jobExecution.getJobInstance().getJobName() + " finished");
      }
    }
  }
}
