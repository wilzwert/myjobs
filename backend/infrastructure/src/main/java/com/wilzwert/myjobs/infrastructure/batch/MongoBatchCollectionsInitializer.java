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
 * Date:16/05/2025
 * Time:14:20
 */
@Component
public class MongoBatchCollectionsInitializer {

    private final MongoTemplate mongoTemplate;

    public MongoBatchCollectionsInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void initCollections() {
        // collections
        if(!mongoTemplate.collectionExists("BATCH_JOB_INSTANCE")) {
            mongoTemplate.createCollection("BATCH_JOB_INSTANCE");
        }
        if(!mongoTemplate.collectionExists("BATCH_JOB_EXECUTION")) {
            mongoTemplate.createCollection("BATCH_JOB_EXECUTION");
        }
        if(!mongoTemplate.collectionExists("BATCH_STEP_EXECUTION")) {
            mongoTemplate.createCollection("BATCH_STEP_EXECUTION");
        }
        // sequences
        if(!mongoTemplate.collectionExists("BATCH_SEQUENCES")) {
            mongoTemplate.createCollection("BATCH_SEQUENCES");
        }

        Document seqInstance = new Document(Map.of("_id", "BATCH_JOB_INSTANCE_SEQ", "count", 0L));
        mongoTemplate.getCollection("BATCH_SEQUENCES")
            .replaceOne(
                    Filters.eq("_id", "BATCH_JOB_INSTANCE_SEQ"),
                    seqInstance,
                    new ReplaceOptions().upsert(true)
            );

        Document seqExecution = new Document(Map.of("_id", "BATCH_JOB_EXECUTION_SEQ", "count", 0L));
        mongoTemplate.getCollection("BATCH_SEQUENCES")
                .replaceOne(
                        Filters.eq("_id", "BATCH_JOB_EXECUTION_SEQ"),
                        seqExecution,
                        new ReplaceOptions().upsert(true)
                );

        Document seqStep = new Document(Map.of("_id", "BATCH_STEP_EXECUTION_SEQ", "count", 0L));
        mongoTemplate.getCollection("BATCH_SEQUENCES")
                .replaceOne(
                        Filters.eq("_id", "BATCH_STEP_EXECUTION_SEQ"),
                        seqStep,
                        new ReplaceOptions().upsert(true)
                );

        // indices
        mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                .ensureIndex(new Index().on("jobName", Sort.Direction.ASC).named("job_name_idx"));
        mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                .ensureIndex(new Index().on("jobName", Sort.Direction.ASC)
                        .on("jobKey", Sort.Direction.ASC)
                        .named("job_name_key_idx"));
        mongoTemplate.indexOps("BATCH_JOB_INSTANCE")
                .ensureIndex(new Index().on("jobInstanceId", Sort.Direction.DESC).named("job_instance_idx"));
        mongoTemplate.indexOps("BATCH_JOB_EXECUTION")
                .ensureIndex(new Index().on("jobInstanceId", Sort.Direction.ASC).named("job_instance_idx"));
        mongoTemplate.indexOps("BATCH_JOB_EXECUTION")
                .ensureIndex(new Index().on("jobInstanceId", Sort.Direction.ASC)
                        .on("status", Sort.Direction.ASC)
                        .named("job_instance_status_idx"));
        mongoTemplate.indexOps("BATCH_STEP_EXECUTION")
                .ensureIndex(new Index().on("stepExecutionId", Sort.Direction.ASC).named("step_execution_idx"));
    }
}
