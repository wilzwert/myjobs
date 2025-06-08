package com.wilzwert.myjobs.infrastructure.batch;


import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventProcessor;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventReader;
import com.wilzwert.myjobs.infrastructure.event.IntegrationEventWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * @author Wilhelm Zwertvaegher
 * Batch to dispatch integration events
 */
@Configuration
@Slf4j
public class IntegrationEventDispatchBatchConfiguration {

    private final IntegrationEventReader integrationEventReader;

    private final IntegrationEventProcessor integrationEventProcessor;

    private final IntegrationEventWriter integrationEventWriter;

    IntegrationEventDispatchBatchConfiguration(IntegrationEventReader integrationEventReader, IntegrationEventProcessor integrationEventProcessor, IntegrationEventWriter integrationEventWriter) {
        this.integrationEventReader = integrationEventReader;
        this.integrationEventProcessor = integrationEventProcessor;
        this.integrationEventWriter = integrationEventWriter;
    }

    @Bean
    public Job integrationEventDispatchJob(Step integrationEventDispatchStep, JobRepository jobRepository) {
        return new JobBuilder("IntegrationEventDispatch", jobRepository)
                .start(integrationEventDispatchStep)
                .preventRestart()
                .build();
    }

    @Bean
    public Step integrationEventDispatchStep(JobRepository jobRepository, MongoTransactionManager transactionManager) {
        return new StepBuilder("integrationEventStep", jobRepository)
                .<IntegrationEvent, IntegrationEvent>chunk(10, transactionManager)
                .reader(integrationEventReader)
                .processor(integrationEventProcessor)
                .writer(integrationEventWriter)
                .build();
    }
}