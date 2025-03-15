package com.wilzwert.myjobs.domain.ports.driven;


import com.wilzwert.myjobs.domain.model.Job;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface JobRepository {
    Optional<Job> findById(UUID id);

    Optional<Job> findByUrlAndUserId(String url, UUID userId);

    Optional<Job> findByIdAndUserId(UUID jobId, UUID userId);

    List<Job> findAllByUserId(UUID userId, int page, int size);


    Job save(Job job);

    void delete(Job job);
}
