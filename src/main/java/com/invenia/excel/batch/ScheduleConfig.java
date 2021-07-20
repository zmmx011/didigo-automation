package com.invenia.excel.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/*@Configuration
@EnableScheduling*/
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer {
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.addTriggerTask(() -> log.info("TEST") , triggerContext -> {
			CronTrigger trigger = new CronTrigger("0/10 * * * * ?");
			return trigger.nextExecutionTime(triggerContext);
		});
	}
}
