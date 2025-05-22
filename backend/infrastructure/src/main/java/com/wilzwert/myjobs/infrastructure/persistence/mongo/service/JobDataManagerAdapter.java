package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.mongodb.bulk.BulkWriteResult;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

    /**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:28
 */
@Component
public class JobDataManagerAdapter implements JobDataManager {
    private final MongoJobRepository mongoJobRepository;
    private final JobMapper jobMapper;
    private final AggregationService aggregationService;
    private final MongoTemplate mongoTemplate;


    public JobDataManagerAdapter(MongoJobRepository mongoJobRepository, JobMapper jobMapper, AggregationService aggregationService, MongoTemplate mongoTemplate) {
        this.mongoJobRepository = mongoJobRepository;
        this.jobMapper = jobMapper;
        this.aggregationService = aggregationService;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Job> findById(JobId jobId) {
        return mongoJobRepository.findById(jobId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByUrlAndUserId(String url, UserId userId) {
        return mongoJobRepository.findByUrlAndUserId(url, userId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByIdAndUserId(JobId jobId, UserId userId) {
        return mongoJobRepository.findByIdAndUserId(jobId.value(), userId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public DomainPage<Job> findPaginated(DomainSpecification specifications, int page, int size) {
        Aggregation aggregation = aggregationService.createAggregationPaginated(specifications, page, size);
        List<MongoJob> jobs = aggregationService.aggregate(aggregation, "jobs", MongoJob.class);

        if(jobs.isEmpty()) {
            return DomainPage.builder(this.jobMapper.toDomain(jobs)).totalElementsCount(0L).currentPage(page).pageSize(size).build();
        }

        long total = aggregationService.getAggregationCount(aggregation, "jobs");
        return DomainPage.builder(this.jobMapper.toDomain(jobs)).totalElementsCount(total).currentPage(page).pageSize(size).build();
    }

    @Override
    public Map<JobId, Job> findMinimal(DomainSpecification specification) {
        Aggregation aggregation = aggregationService.createAggregation(specification);
        return jobMapper.toDomain(aggregationService.aggregate(aggregation, "jobs", MongoJob.class))
                .stream()
                .collect(Collectors.toMap(Job::getId, job -> job));
    }

    @Override
    public Stream<Job> stream(DomainSpecification specification) {
        Aggregation aggregation = aggregationService.createAggregation(specification);
        Stream<MongoJob> stream = aggregationService.stream(aggregation, "jobs", MongoJob.class);
        return stream.map(jobMapper::toDomain).onClose(stream::close);
    }

        @Override
    public Job save(Job job) {
        return this.jobMapper.toDomain(mongoJobRepository.save(this.jobMapper.toEntity(job)));
    }

    @Override
    public Job saveJobAndActivity(Job job, Activity activity) {
        return jobMapper.toDomain(mongoJobRepository.save(jobMapper.toEntity(job)));
    }

    @Override
    public Job saveJobAndAttachment(Job job, Attachment attachment, Activity activity) {
        return jobMapper.toDomain(mongoJobRepository.save(jobMapper.toEntity(job)));
    }

    @Override
    public void delete(Job job) {
        // attachments, activities are nested mongo collections, so there's nothing more to do that delete the job
        mongoJobRepository.delete(jobMapper.toEntity(job));
    }

    @Override
    public Job deleteAttachmentAndSaveJob(Job job, Attachment attachment, Activity activity) {
        // attachments and activities are nested collections, nothing more to do
        return jobMapper.toDomain(mongoJobRepository.save(jobMapper.toEntity(job)));
    }

    // TODO : tests (dont forget to test with empty set)
    @Override
    public BulkDataSaveResult saveAll(Set<Job> jobs) {
        // we chose to throw an exception because it seems like something went wrong if someone tries to save an empty set
        if(jobs.isEmpty()) {
            throw new IllegalArgumentException("jobs must not be empty");
        }

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, MongoJob.class);

        List<MongoJob> mongoJobs = jobMapper.toEntity(jobs.stream().toList());
        for(MongoJob job : mongoJobs) {
            Update update = new Update();
            update.set("followUpReminderSentAt", job.getFollowUpReminderSentAt());
            bulkOps.updateOne(Query.query(Criteria.where("_id").is(job.getId())), update);
        }

        BulkWriteResult result = bulkOps.execute();
        return new BulkDataSaveResult(jobs.size(), result.getModifiedCount(), result.getInsertedCount(), result.getDeletedCount());
    }
}