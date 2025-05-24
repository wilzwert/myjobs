package com.wilzwert.myjobs.infrastructure.batch;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Configure Spring Batch to use mongo repo and explorer
 * @author Wilhelm Zwertvaegher
 */
@Component
public class MongoBatchCollectionsInitializer {

    private final MongoTemplate mongoTemplate;

    public MongoBatchCollectionsInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initCollections() {
        String batchObjInstanceCollection = "BATCH_JOB_INSTANCE";
        String batchJobExecutionCollection = "BATCH_JOB_EXECUTION";
        String batchStepExecutionCollection = "BATCH_STEP_EXECUTION";
        String batchSequencesCollection = "BATCH_SEQUENCES";

        // collections
        if(!mongoTemplate.collectionExists(batchObjInstanceCollection)) {
            mongoTemplate.createCollection(batchObjInstanceCollection);
        }
        if(!mongoTemplate.collectionExists(batchJobExecutionCollection)) {
            mongoTemplate.createCollection(batchJobExecutionCollection);
        }
        if(!mongoTemplate.collectionExists(batchStepExecutionCollection)) {
            mongoTemplate.createCollection(batchStepExecutionCollection);
        }
        // sequences
        if(!mongoTemplate.collectionExists(batchSequencesCollection)) {
            mongoTemplate.createCollection(batchSequencesCollection);
        }
        String countField = "count";
        Document seqInstance = new Document(Map.of("_id", "BATCH_JOB_INSTANCE_SEQ", countField, 0L));
        mongoTemplate.getCollection(batchSequencesCollection)
            .replaceOne(
                    Filters.eq("_id", "BATCH_JOB_INSTANCE_SEQ"),
                    seqInstance,
                    new ReplaceOptions().upsert(true)
            );

        Document seqExecution = new Document(Map.of("_id", "BATCH_JOB_EXECUTION_SEQ", countField, 0L));
        mongoTemplate.getCollection(batchSequencesCollection)
                .replaceOne(
                        Filters.eq("_id", "BATCH_JOB_EXECUTION_SEQ"),
                        seqExecution,
                        new ReplaceOptions().upsert(true)
                );

        Document seqStep = new Document(Map.of("_id", "BATCH_STEP_EXECUTION_SEQ", countField, 0L));
        mongoTemplate.getCollection(batchSequencesCollection)
                .replaceOne(
                        Filters.eq("_id", "BATCH_STEP_EXECUTION_SEQ"),
                        seqStep,
                        new ReplaceOptions().upsert(true)
                );

        // indices
        mongoTemplate.indexOps(batchObjInstanceCollection)
                .ensureIndex(new Index().on("jobName", Sort.Direction.ASC).named("job_name_idx"));
        mongoTemplate.indexOps(batchObjInstanceCollection)
                .ensureIndex(new Index().on("jobName", Sort.Direction.ASC)
                        .on("jobKey", Sort.Direction.ASC)
                        .named("job_name_key_idx"));

        String jobInstanceIdField = "jobInstanceId";
        mongoTemplate.indexOps(batchObjInstanceCollection)
                .ensureIndex(new Index().on(jobInstanceIdField, Sort.Direction.DESC).named("job_instance_idx"));
        mongoTemplate.indexOps(batchJobExecutionCollection)
                .ensureIndex(new Index().on(jobInstanceIdField, Sort.Direction.ASC).named("job_instance_idx"));
        mongoTemplate.indexOps(batchJobExecutionCollection)
                .ensureIndex(new Index().on(jobInstanceIdField, Sort.Direction.ASC)
                        .on("status", Sort.Direction.ASC)
                        .named("job_instance_status_idx"));
        mongoTemplate.indexOps(batchStepExecutionCollection)
                .ensureIndex(new Index().on("stepExecutionId", Sort.Direction.ASC).named("step_execution_idx"));
    }
}
