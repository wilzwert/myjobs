package com.wilzwert.myjobs.infrastructure.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
public class JobsRemindersScheduler {

    private final JobOperator jobOperator;
    private final Job jobReminderJob;

    public JobsRemindersScheduler(JobOperator jobOperator, Job jobReminderJob) {
        this.jobOperator = jobOperator;
        this.jobReminderJob = jobReminderJob;
    }

    @Scheduled(cron = "0 0 6 * * ?") // daily at 6 am
    public void scheduleJobsReminders()  {
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobOperator.start(jobReminderJob, params);
            log.info("Job reminders scheduled run, started at {}, ended at {}, exited with {}", execution.getStartTime(), execution.getEndTime(), execution.getExitStatus());
        }
        catch (Exception e) {
            log.info("Job reminders scheduled throw an exception {}", e.getMessage());
        }
    }
}