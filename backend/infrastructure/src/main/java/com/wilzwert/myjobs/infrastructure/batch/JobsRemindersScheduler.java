package com.wilzwert.myjobs.infrastructure.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class JobsRemindersScheduler {

    private final JobLauncher jobLauncher;
    private final Job jobReminderJob;

    public JobsRemindersScheduler(JobLauncher jobLauncher, Job jobReminderJob) {
        this.jobLauncher = jobLauncher;
        this.jobReminderJob = jobReminderJob;
    }

    @Scheduled(cron = "0 0 6 * * ?") // daily at 6 am
    public void scheduleJobsReminders()  {
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(jobReminderJob, params);
            log.info("Job reminders scheduled run, started at {}, ended at {}, exited with {}", execution.getStartTime(), execution.getEndTime(), execution.getExitStatus());
        }
        catch (Exception e) {
            log.info("Job reminders scheduled throw an exception {}", e.getMessage());
        }
    }
}