package com.wilzwert.myjobs.infrastructure.batch;


/**
 * @author Wilhelm Zwertvaegher
 */
/*
@Component
public class BatchMetadataPurgeTasklet implements Tasklet {

    private final BatchMetadataPurgeService purgeService;

    public BatchMetadataPurgeTasklet(BatchMetadataPurgeService purgeService) {
        this.purgeService = purgeService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        int deleted = purgeService.purgeBatchMetadataOlderThan(Duration.ofDays(30));
        System.out.println("Purge done, total rows deleted: " + deleted);
        return RepeatStatus.FINISHED;
    }
}
*/