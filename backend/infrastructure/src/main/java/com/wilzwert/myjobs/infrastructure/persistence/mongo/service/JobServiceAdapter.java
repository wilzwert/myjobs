    package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.shared.querying.DomainQueryingOperation;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:28
 */
@Component
public class JobServiceAdapter implements JobService {
    private final MongoJobRepository mongoJobRepository;
    private final JobMapper jobMapper;
    private final AggregationService aggregationService;


    public JobServiceAdapter(MongoJobRepository mongoJobRepository, JobMapper jobMapper, AggregationService aggregationService) {
        this.mongoJobRepository = mongoJobRepository;
        this.jobMapper = jobMapper;
        this.aggregationService = aggregationService;
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

    /**
     *
     * @param sortString the sort string e.g. "createdAt,desc"
     * @return a sort to be used in a PageRequest
     */
    private Sort getSort(String sortString) {
        if(sortString == null || sortString.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        var sortOrder = sortString.split(",");
        Sort sort = Sort.by(sortOrder[0]);
        if(sortOrder[1].equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        else {
            sort = sort.ascending();
        }
        return sort;
    }

    @Override
    public DomainPage<Job> findAllByUserIdPaginated(UserId userId, int page, int size, JobStatus status, String sortString) {
        Sort sort = getSort(sortString);

        if(status != null) {
            return this.jobMapper.toDomain(mongoJobRepository.findByUserIdAndStatus(userId.value(), status, PageRequest.of(page, size, sort)));
        }
        return this.jobMapper.toDomain(mongoJobRepository.findByUserId(userId.value(), PageRequest.of(page, size, sort)));
    }


    // this could be used to load late follow up Jobs e.g. in the context of a batch to send reminders
    // although in the context of this app it would break the DDD because only the domain should know
    // what a late job is
    // For now and for simplicity the domain use case handling these reminders will handle the loading
    // through regular operations (load a user list, iterate through it and load each user's late jobs to send reminders)
    // this method is commented out at the time to keep a trace of the aggregation that could be used
    /*
    public List<Job> findLateFollowUp(String sortString) {
        Sort sort = getSort(sortString);
        Instant now = Instant.now();

        Aggregation aggregation = Aggregation.newAggregation(
                // join "user" collection
                Aggregation.lookup("user", "userId", "_id", "user"),

                // exclude users with null jobFollowUpReminderDays
                Aggregation.match(Criteria.where("user.jobFollowUpReminderDays").ne(null)),

                // get only active jobs
                Aggregation.match(Criteria.where("status").in(JobStatus.activeStatuses())),

                // unwind "user" field (array => unique object)
                Aggregation.unwind("user"),
                // add a thresholdDate field = now - user.jobFollowUpReminderDays
                Aggregation.addFields()
                        .addFieldWithValue(
                        "thresholdDate",
                            ArithmeticOperators.Subtract.valueOf(now.toEpochMilli()).subtract(
                                    ArithmeticOperators.Multiply.valueOf("user.jobFollowUpReminderDays")
                                            .multiplyBy(86400000)
                            )
                        ).build(),
                // filter jobs where statusUpdatedAt < thresholdDate
                Aggregation.match(Criteria.where("statusUpdatedAt").lt("thresholdDate")),
                aggregationService.getSortOperation(sortString)
        );

        AggregationResults<Job> results = mongoTemplate.aggregate(aggregation, "job", Job.class);
        return results.getMappedResults();
    }*/

    @Override
    public DomainPage<Job> findByUserPaginated(User user, List<DomainQueryingOperation> queryingOperations, int page, int size, String sortString) {
        Aggregation aggregation = aggregationService.createAggregationPaginated(user, queryingOperations, sortString, page, size);
        List<MongoJob> jobs = aggregationService.aggregate(aggregation, "jobs", MongoJob.class);

        if(jobs.isEmpty()) {
            return DomainPage.builder(this.jobMapper.toDomain(jobs)).totalElementsCount(0L).build();
        }

        long total = aggregationService.getAggregationCount(aggregation, "jobs");
        return DomainPage.builder(this.jobMapper.toDomain(jobs)).totalElementsCount(total).build();
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
        mongoJobRepository.delete(jobMapper.toEntity(job));
    }

    @Override
    public Job deleteAttachment(Job job, Attachment attachment, Activity activity) {
        return jobMapper.toDomain(mongoJobRepository.save(jobMapper.toEntity(job)));
    }
}