package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;

import com.wilzwert.myjobs.core.domain.model.JobStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:50
 */
@Repository
public interface MongoJobRepository extends MongoRepository<MongoJob, String> {
    Optional<MongoJob> findById(UUID jobId);
    Optional<MongoJob> findByUrlAndUserId(String url, UUID userId);
    Optional<MongoJob> findByIdAndUserId(UUID jobId, UUID userId);
    Page<MongoJob> findByUserId(UUID userId, @Nullable Pageable pageable);

    // @Query("{ '_id': ?0, 'status': ?1 }")
    Page<MongoJob> findByUserIdAndStatus(UUID userId, JobStatus status, @Nullable Pageable pageable);
    void deleteByUserId(UUID userId);
    // Page<MongoJob> findPagedByUserId(UUID userId, @Nullable Pageable pageable);

}