package com.wilzwert.myjobs.infrastructure.batch;


import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBatchResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:16/05/2025
 * Time:13:24
 * Batch to send jobs reminders
 * As an exercice, I decided to let the domain handle the chunking
 * Therefore there is no ItemReader or ItemWriter
 * The only responsibility of the infra is to provide a chunk size and log results
 */

@Configuration
@Slf4j
public class JobsRemindersBatchConfiguration {
    @Bean
    public Job jobReminderJob(Step jobReminderStep, JobRepository jobRepository) {
        return new JobBuilder("jobReminderJob", jobRepository)
                .start(jobReminderStep)
                .preventRestart()
                .build();
    }

    @Bean
    public Step jobReminderStep(Tasklet jobReminderTasklet, JobRepository jobRepository, MongoTransactionManager transactionManager) {
        return new StepBuilder("jobReminderStep", jobRepository)
                .tasklet(jobReminderTasklet, transactionManager)
                .build();
    }

    @Bean
    public Tasklet jobReminderTasklet(SendJobsRemindersUseCase useCase) {
        return (contribution, chunkContext) -> {
            try {
                List<UsersJobsRemindersBatchResult> results = useCase.sendJobsReminders(1);
                int totalSendErrors = results.stream().mapToInt(UsersJobsRemindersBatchResult::getSendErrorsCount).sum();
                int totalSaveErrors = results.stream().mapToInt(UsersJobsRemindersBatchResult::getSaveErrorsCount).sum();
                if(totalSendErrors > 0 || totalSaveErrors > 0) {
                    log.warn("SendJobReminders batch run : {} chunks, {} send errors, {} save errors", results.size(), totalSaveErrors, totalSendErrors);
                }
                else {
                    int totalUsersReminded = results.stream().mapToInt(UsersJobsRemindersBatchResult::getUsersCount).sum();
                    int totalJobsReminded = results.stream().mapToInt(UsersJobsRemindersBatchResult::getJobsCount).sum();
                    log.info("SendJobReminders batch run : {} chunks, {} users, {} jobs", results.size(), totalUsersReminded, totalJobsReminded);
                }
            }
            catch (Exception e) {
                log.error("SendJobReminders batch throw an exception", e);
            }
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        };
    }
}

