package com.invenia.excel.batch;

import java.time.LocalDateTime;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobLauncher {

  private final BatchJob batchJob;
  private final JobLauncher jobLauncher;

  public void executeJob(Function<BatchJob, Job> func, String fromDateStr, String toDateStr) {
    try {
      jobLauncher.run(func.apply(batchJob), new JobParametersBuilder()
          .addString("dateTime", LocalDateTime.now().toString())
          .addString("fromDateStr", fromDateStr)
          .addString("toDateStr", toDateStr)
          .toJobParameters()
      );
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }
}
