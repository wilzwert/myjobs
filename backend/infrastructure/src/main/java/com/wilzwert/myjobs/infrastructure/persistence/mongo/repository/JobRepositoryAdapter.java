package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;


import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:28
 */
@Component
public class JobRepositoryAdapter implements JobRepository {
    private final SpringMongoJobRepository springMongoJobRepository;
    private final JobMapper jobMapper;

    public JobRepositoryAdapter(SpringMongoJobRepository springMongoJobRepository, JobMapper jobMapper) {
        this.springMongoJobRepository = springMongoJobRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public Optional<Job> findById(JobId jobId) {
        return springMongoJobRepository.findById(jobId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByUrlAndUserId(String url, UserId userId) {
        return springMongoJobRepository.findByUrlAndUserId(url, userId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByIdAndUserId(JobId jobId, UserId userId) {
        return springMongoJobRepository.findByIdAndUserId(jobId.value(), userId.value()).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public List<Job> findAllByUserId(UserId userId, int page, int size) {
        return this.jobMapper.toDomain(springMongoJobRepository.findByUserId(userId.value(), PageRequest.of(page, size)));
    }

    @Override
    public Job save(Job job) {
        return this.jobMapper.toDomain(springMongoJobRepository.save(this.jobMapper.toEntity(job)));
    }

    @Override
    public void delete(Job job) {
        springMongoJobRepository.delete(jobMapper.toEntity(job));
    }
}
