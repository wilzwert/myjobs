package com.wilzwert.myjobs.infrastructure.batch;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class JobsRemindersScheduler {

    private final JobLauncher jobLauncher;
    private final Job jobReminderJob;

    public JobsRemindersScheduler(JobLauncher jobLauncher, Job jobReminderJob) {
        this.jobLauncher = jobLauncher;
        this.jobReminderJob = jobReminderJob;
    }

    @Scheduled(cron = "0 0 6 * * ?") // daily at 6 am
    public void scheduleJobsReminders()  {
        System.out.println("==> [SCHEDULED JOB TRIGGERED] at " + LocalDateTime.now());
        JobParameters params = new JobParametersBuilder()
                .addString("run.id", UUID.randomUUID().toString(), true)
                .toJobParameters();
        try {
            JobExecution execution = jobLauncher.run(jobReminderJob, params);
        }
        catch (Exception e) {
            // TODO : log instead of print
            e.printStackTrace();
        }
    }
}