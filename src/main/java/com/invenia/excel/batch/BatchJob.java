package com.invenia.excel.batch;

import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.converter.ExcelConverter;
import com.invenia.excel.selenium.Automation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJob {

  private final JobBuilderFactory jobBuilderFactory;
  private final ExcelConverter excelConverter;
  private final Automation automation;
  private final BatchConfig batchConfig;
  private final BatchStep step;

  // 품목 등록
  public Job itemCodeJob() {
    return jobBuilderFactory.get("품목 등록")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep())
        .next(step.itemCodeDownloadStep(true)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.siteDownloadStep(null)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeUploadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .end()
        .build();
  }

  // 미등록 거래처 확인
  public Job customerCheckJob() {
    return jobBuilderFactory.get("거래처 확인")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep())
        .next(step.customerDownloadStep(true)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.siteDownloadStep(null)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.customerCheckStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .end()
        .build();
  }

  // 수주 등록
  public Job contractOrderJob() {
    return jobBuilderFactory.get("수주 등록")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep())
        .next(step.itemCodeDownloadStep(true)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.customerDownloadStep(false)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderDownloadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.siteDownloadStep(null)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.customerCheckStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeUploadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderUploadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .end()
        .build();
  }

  // 발주 데이터
  public Job purchaseOrderJob() {
    return jobBuilderFactory.get("발주 변환")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep())
        .next(step.itemCodeDownloadStep(true)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.siteDownloadStep(null)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.purchaseOrderConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .end()
        .build();
  }

  // All
  public Job allProcessJob() {
    return jobBuilderFactory.get("전체")
        .preventRestart()
        .listener(new ConvertJobExecutionListener())
        .start(step.initStep())
        .next(step.itemCodeDownloadStep(true)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.customerDownloadStep(false)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderDownloadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.siteDownloadStep(null)).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.customerCheckStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.purchaseOrderConvertStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.itemCodeUploadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .next(step.contractOrderUploadStep()).on(ExitStatus.FAILED.getExitCode()).fail()
        .end()
        .build();
  }

  private class ConvertJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
      automation.setup();
      batchConfig.setTabIndex(0);
      log.info("runCozy : " + batchConfig.getRunCozy() +
          " runKd : " + batchConfig.getRunKd() +
          " runMall : " + batchConfig.getRunMall());
      log.info(jobExecution.getJobInstance().getJobName() + " started");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
      automation.closeAutomation();
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
