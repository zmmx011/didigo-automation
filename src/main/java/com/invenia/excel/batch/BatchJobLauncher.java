package com.invenia.excel.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchJobLauncher {
	private final BatchJob batchJob;
	private final JobLauncher jobLauncher;

	public void executeJob(Function<BatchJob, Job> func) {
		try {
			jobLauncher.run(func.apply(batchJob), getJobParameters());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

	private JobParameters getJobParameters() {
		return new JobParametersBuilder()
				.addString("dateTime", LocalDateTime.now().toString())
				.toJobParameters();
	}
}
