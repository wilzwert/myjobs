package com.wilzwert.myjobs.core.domain.model.job.ports.driven;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface JobDataManager {

    Optional<Job> findById(JobId id);

    Optional<Job> findByUrlAndUserId(String url, UserId userId);

    Optional<Job> findByIdAndUserId(JobId jobId, UserId userId);

    DomainPage<Job> findPaginated(DomainSpecification specifications, int page, int itemsPerPage);

    Map<JobId, Job> findMinimal(DomainSpecification specification);

    Stream<Job> stream(DomainSpecification specification);

    Job save(Job job);

    Job saveJobAndActivity(Job job, Activity activity);

    Job saveJobAndAttachment(Job job, Attachment attachment, Activity activity);

    /**
     * Deletes the Job
     * Important : all related entities MUST be deleted too
     * @param job the Job to delete
     */
    void delete(Job job);

    /**
     *
     * @param job the Job
     * @param deletedAttachment the attachment that is being deleted
     * @param createdActivity the activity created by the attachment deletion
     * @return the Job
     */
    Job deleteAttachmentAndSaveJob(Job job, Attachment deletedAttachment, Activity createdActivity);

    BulkDataSaveResult saveAll(Set<Job> job);
}
