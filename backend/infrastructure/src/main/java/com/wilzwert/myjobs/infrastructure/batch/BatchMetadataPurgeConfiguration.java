package com.wilzwert.myjobs.infrastructure.batch;


/**
 * @author Wilhelm Zwertvaegher
 * Date:16/05/2025
 * Time:13:48
 */
/*
public class BatchMetadataPurgeConfiguration {
    @Bean
    public Step purgeBatchMetadataStep(StepBuilderFactory stepBuilderFactory,
                                       BatchMetadataPurgeTasklet tasklet) {
        return stepBuilderFactory.get("purgeBatchMetadataStep")
                .tasklet(tasklet)
                .build();
    }

    @Bean
    public Job purgeBatchMetadataJob(JobBuilderFactory jobBuilderFactory, Step purgeBatchMetadataStep) {
        return jobBuilderFactory.get("purgeBatchMetadataJob")
                .start(purgeBatchMetadataStep)
                .build();
    }
}
*/