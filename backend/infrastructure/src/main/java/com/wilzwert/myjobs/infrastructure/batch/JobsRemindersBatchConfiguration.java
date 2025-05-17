package com.wilzwert.myjobs.infrastructure.batch;


import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendJobsRemindersUseCase;
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
 * Date:16/05/2025
 * Time:13:24
 */

@Configuration
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
            System.out.println("-------------------------------------Batch : step stepName["+chunkContext.getStepContext().getStepName()+"] instanceId["+chunkContext.getStepContext().getJobInstanceId()+"]");
            try {
                System.out.println("-------------------------------------reminder result "+useCase.sendJobsReminders(1));
            }
            catch (Exception e) {
                // TODO log
            }
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        };
    }
}

