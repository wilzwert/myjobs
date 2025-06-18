package com.wilzwert.myjobs.infrastructure.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestBatchConfig {

    @Autowired
    private Job integrationEventDispatchJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        JobLauncherTestUtils utils = new JobLauncherTestUtils();
        utils.setJobLauncher(jobLauncher);
        utils.setJob(integrationEventDispatchJob);
        utils.setJobRepository(jobRepository);

        return utils;
    }

    @Bean
    public JobRepositoryTestUtils jobRepositoryTestUtils() {
        return new JobRepositoryTestUtils(jobRepository);
    }
}