package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;

import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */
@Repository
public interface MongoJobRepository extends MongoRepository<MongoJob, String> {
    Optional<MongoJob> findById(UUID jobId);
    Optional<MongoJob> findByUrlAndUserId(String url, UUID userId);
    Optional<MongoJob> findByIdAndUserId(UUID jobId, UUID userId);
    Page<MongoJob> findByUserId(UUID userId, @Nullable Pageable pageable);
    List<MongoJob> findByUserId(UUID userId);
    Page<MongoJob> findByUserIdAndStatus(UUID userId, JobStatus status, @Nullable Pageable pageable);
    void deleteByUserId(UUID userId);

}