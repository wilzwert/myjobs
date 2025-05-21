package com.wilzwert.myjobs.core.domain.model.job.ports.driven;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkServiceSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface JobService {

    Optional<Job> findById(JobId id);

    Optional<Job> findByUrlAndUserId(String url, UserId userId);

    Optional<Job> findByIdAndUserId(JobId jobId, UserId userId);

    DomainPage<Job> findPaginated(DomainSpecification specifications, int page, int size);

    Map<JobId, Job> findMinimal(DomainSpecification specification);

    Stream<Job> stream(DomainSpecification specification);

    Job save(Job job);

    Job saveJobAndActivity(Job job, Activity activity);

    Job saveJobAndAttachment(Job job, Attachment attachment, Activity activity);

    void delete(Job job);

    Job deleteAttachment(Job job, Attachment attachment, Activity activity);

    BulkServiceSaveResult saveAll(Set<Job> job);
}
