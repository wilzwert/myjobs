package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;


import com.wilzwert.myjobs.domain.model.Job;
import com.wilzwert.myjobs.domain.ports.driven.JobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:28
 */
@Component
public class MongoJobRepository implements JobRepository {
    private final SpringMongoJobRepository springMongoJobRepository;
    private final JobMapper jobMapper;

    public MongoJobRepository(SpringMongoJobRepository springMongoJobRepository, JobMapper jobMapper) {
        this.springMongoJobRepository = springMongoJobRepository;
        this.jobMapper = jobMapper;
    }

    @Override
    public Optional<Job> findById(UUID id) {
        return springMongoJobRepository.findById(id).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByUrlAndUserId(String url, UUID userId) {
        return springMongoJobRepository.findByUrlAndUserId(url, userId).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<Job> findByIdAndUserId(UUID jobId, UUID userId) {
        return springMongoJobRepository.findByIdAndUserId(jobId, userId).map(jobMapper::toDomain).or(Optional::empty);
    }

    @Override
    public List<Job> findAllByUserId(UUID userId, int page, int size) {
        return this.jobMapper.toDomain(springMongoJobRepository.findByUserId(userId, PageRequest.of(page, size)));
    }

    @Override
    public Job save(Job job) {
        MongoJob mongoJob = this.jobMapper.toEntity(job);
        mongoJob.setUserId(job.getUserId());
        return this.jobMapper.toDomain(springMongoJobRepository.save(this.jobMapper.toEntity(job)));
    }

    @Override
    public void delete(Job job) {
        springMongoJobRepository.delete(jobMapper.toEntity(job));
    }
}
