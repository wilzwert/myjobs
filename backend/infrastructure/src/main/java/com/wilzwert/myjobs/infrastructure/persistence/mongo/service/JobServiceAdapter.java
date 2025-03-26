package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public JobServiceAdapter(MongoJobRepository mongoJobRepository, JobMapper jobMapper) {
        this.mongoJobRepository = mongoJobRepository;
        this.jobMapper = jobMapper;
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
    public DomainPage<Job> findAllByUserId(UserId userId, int page, int size, JobStatus status) {
        if(status != null) {
            return this.jobMapper.toDomain(mongoJobRepository.findByUserIdAndStatus(userId.value(), status, PageRequest.of(page, size, Sort.by("createdAt").descending())));
        }
        return this.jobMapper.toDomain(mongoJobRepository.findByUserId(userId.value(), PageRequest.of(page, size, Sort.by("createdAt").descending())));
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
