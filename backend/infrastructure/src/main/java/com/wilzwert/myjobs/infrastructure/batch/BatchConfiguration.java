package com.wilzwert.myjobs.infrastructure.batch;


import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MongoJobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MongoJobRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configure Spring Batch to use mongo repo and explorer
 * @author Wilhelm Zwertvaegher
 */
@Configuration
@ConditionalOnProperty(name = "application.batch.enabled", havingValue = "true")
@EnableBatchProcessing
public class BatchConfiguration {
    @Bean
    public JobRepository jobRepository(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) throws Exception {
        MongoJobRepositoryFactoryBean factory = new MongoJobRepositoryFactoryBean();
        factory.setMongoOperations(mongoTemplate);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JobExplorer jobExplorer(MongoTemplate mongoTemplate, MongoTransactionManager transactionManager) throws Exception {
        MongoJobExplorerFactoryBean factory = new MongoJobExplorerFactoryBean();
        factory.setMongoOperations(mongoTemplate);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        return (job, jobParameters) -> {
            JobExecution execution = jobRepository.createJobExecution(job.getName(), jobParameters);
            job.execute(execution);
            return execution;
        };
    }
}
