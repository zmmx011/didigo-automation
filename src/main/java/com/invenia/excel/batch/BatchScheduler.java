package com.invenia.excel.batch;

import com.invenia.excel.web.entity.RunEnvironment;
import com.invenia.excel.web.repository.RunEnvironmentRepository;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

  private final TaskScheduler scheduler;
  private final BatchJobLauncher batchJobLauncher;
  private final RunEnvironmentRepository runEnvironmentRepository;
  private ScheduledFuture<?> future;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    if (runEnvironmentRepository.findById("auto").isEmpty()) {
      RunEnvironment env = RunEnvironment.builder()
          .type("auto")
          .cron("0 10 09 * * MON") // 매주 월요일 09시 10분
          .period(6) // 일주일 전 데이터 수집
          .build();
      runEnvironmentRepository.save(env);
    }
    start();
  }

  public void start() {
    RunEnvironment environment = getRunEnvironment();
    future = scheduler.schedule(
        () -> batchJobLauncher.executeJob(BatchJob::allProcessJob, environment.getType()),
        new CronTrigger(environment.getCron())
    );
  }

  public void changeCron() {
    if (future != null) {
      future.cancel(true);
    }
    future = null;
    start();
  }

  private RunEnvironment getRunEnvironment() {
    return runEnvironmentRepository.findById("auto")
        .orElseThrow(() -> new EntityNotFoundException("auto"));
  }
}
