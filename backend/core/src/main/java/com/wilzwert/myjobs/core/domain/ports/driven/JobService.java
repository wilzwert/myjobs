package com.wilzwert.myjobs.core.domain.ports.driven;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

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

    DomainPage<Job> findAllByUserId(UserId userId, int page, int size, JobStatus status, String sort);

    Job save(Job job);

    Job saveJobAndActivity(Job job, Activity activity);

    Job saveJobAndAttachment(Job job, Attachment attachment, Activity activity);

    void delete(Job job);

    Job deleteAttachment(Job job, Attachment attachment, Activity activity);
}
