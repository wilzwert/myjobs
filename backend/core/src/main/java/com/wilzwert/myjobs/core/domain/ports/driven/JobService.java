package com.wilzwert.myjobs.core.domain.ports.driven;


import com.wilzwert.myjobs.core.domain.model.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface JobService {
    Optional<Job> findById(JobId id);

    Optional<Job> findByUrlAndUserId(String url, UserId userId);

    Optional<Job> findByIdAndUserId(JobId jobId, UserId userId);

    DomainPage<Job> findAllByUserId(UserId userId, int page, int size);

    Job save(Job job);

    Job saveJobAndActivity(Job job, Activity activity);

    void delete(Job job);
}
