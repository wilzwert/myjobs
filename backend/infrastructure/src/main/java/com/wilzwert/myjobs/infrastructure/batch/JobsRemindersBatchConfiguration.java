package com.wilzwert.myjobs.infrastructure.batch;


import com.wilzwert.myjobs.infrastructure.batch.service.SendJobsRemindersBatchRunner;
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

/**
 * @author Wilhelm Zwertvaegher
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
    public Tasklet jobReminderTasklet(SendJobsRemindersBatchRunner runner) {
        return (contribution, chunkContext) -> {
            UsersJobsBatchExecutionResult result = runner.run();
            // store result in context in case access is needed later
            chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .put("usersJobsBatchExecutionResult", result);
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        };
    }
}

