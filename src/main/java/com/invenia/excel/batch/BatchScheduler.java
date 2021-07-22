package com.invenia.excel.batch;

import com.invenia.excel.web.entity.RunEnvironment;
import com.invenia.excel.web.repository.RunEnvironmentRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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

  private final String RUN_TYPE = "auto";
  private final TaskScheduler scheduler;
  private final BatchJobLauncher launcher;
  private final RunEnvironmentRepository runEnvironmentRepository;
  private ScheduledFuture<?> future;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    if (runEnvironmentRepository.findById(RUN_TYPE).isEmpty()) {
      RunEnvironment env = RunEnvironment.builder()
          .type(RUN_TYPE)
          .cron("0 10 09 * * MON") // 매주 월요일 09시 10분
          .period(6) // 일주일 전 데이터 수집
          .build();
      runEnvironmentRepository.save(env);
    }
    //start();
  }

  public void start() {
    RunEnvironment environment = runEnvironmentRepository.findById(RUN_TYPE)
        .orElseThrow(() -> new EntityNotFoundException(RUN_TYPE));
    LocalDate today = LocalDate.now();
    DayOfWeek todayOfWeek = today.getDayOfWeek();
    LocalDate fromDate;
    LocalDate toDate;
    int period = environment.getPeriod();
    if (period == 1) {
      fromDate = today.minusDays(period);
      toDate = today.minusDays(period);
    } else {
      fromDate = today.minusDays(period + todayOfWeek.getValue());
      toDate = today.minusDays(todayOfWeek.getValue());
    }
    future = scheduler.schedule(() -> launcher
            .executeJob(BatchJob::allProcessJob, fromDate.toString(), toDate.toString()),
        new CronTrigger(environment.getCron()));
  }

  public void clear() {
    if (future != null) {
      future.cancel(true);
    }
    future = null;
  }
}
