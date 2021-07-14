package com.invenia.excel.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.batch.runtime.BatchStatus;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {
	private final BatchJobLauncher batchJobLauncher;

	// 매주 월요일 05시
	@Scheduled(cron = "0 10 09 * * MON")
	public void everyMonDay() {
		batchJobLauncher.executeJob(BatchJob::allProcessJob);
	}
}
