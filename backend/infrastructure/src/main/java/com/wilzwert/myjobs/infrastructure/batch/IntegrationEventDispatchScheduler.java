package com.wilzwert.myjobs.infrastructure.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "application.batch.enabled", havingValue = "true")
@Slf4j
public class IntegrationEventDispatchScheduler {

    private final JobOperator jobOperator;
    private final Job integrationEventDispatchJob;

    public IntegrationEventDispatchScheduler(JobOperator jobOperator, Job integrationEventDispatchJob) {
        this.jobOperator = jobOperator;
        this.integrationEventDispatchJob = integrationEventDispatchJob;
    }

    @Scheduled(cron = "0 */5 * * * ?") // every 5 minutes
    public void scheduleIntegrationEventDispatch()  {
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobOperator.start(integrationEventDispatchJob, params);
            log.info(
                    "Integration event dispatch scheduled run, started at {}, ended at {}, exited with {}",
                    execution.getStartTime(),
                    execution.getEndTime(),
                    execution.getExitStatus()
            );
        }
        catch (Exception e) {
            log.info("Integration event dispatch scheduled threw an exception {}", e.getMessage());
        }
    }
}