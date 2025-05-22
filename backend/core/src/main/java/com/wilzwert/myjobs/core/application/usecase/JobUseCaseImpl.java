package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentNotFoundException;
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.DownloadAttachmentUseCase;
import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.*;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.GetUserJobUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.GetUserJobsUseCase;
import com.wilzwert.myjobs.core.domain.model.job.service.JobEnricher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class JobUseCaseImpl implements CreateJobUseCase, GetUserJobUseCase, UpdateJobUseCase, UpdateJobStatusUseCase, UpdateJobRatingUseCase, DeleteJobUseCase, GetUserJobsUseCase, AddActivityToJobUseCase, UpdateActivityUseCase, AddAttachmentToJobUseCase, DownloadAttachmentUseCase, DeleteAttachmentUseCase {

    private final JobDataManager jobDataManager;

    private final UserDataManager userDataManager;

    private final FileStorage fileStorage;

    private final HtmlSanitizer htmlSanitizer;

    private final JobEnricher jobEnricher = new JobEnricher();

    public JobUseCaseImpl(JobDataManager jobDataManager, UserDataManager userDataManager, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        this.jobDataManager = jobDataManager;
        this.userDataManager = userDataManager;
        this.fileStorage = fileStorage;
        this.htmlSanitizer = htmlSanitizer;
    }

    @Override
    public Job createJob(CreateJobCommand command) {
        Optional<User> user = userDataManager.findById(command.userId());
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        Job jobToCreate = Job.create(
                Job.builder()
                .url(command.url())
                .title(command.title())
                .company(command.company())
                .description(command.description())
                .profile(command.profile())
                .salary(command.salary())
                .userId(user.get().getId())
        );
        Job job = user.get().addJob(jobToCreate);
        userDataManager.saveUserAndJob(user.get(), job);
        return job;
    }

    @Override
    public void deleteJob(DeleteJobCommand command) {
        Optional<User> foundUser = userDataManager.findById(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }
        Job job = foundJob.get();

        // delete attachments' files
        job.getAttachments().forEach(attachment -> {
            try {
                fileStorage.delete(attachment.getFileId());
            }
            catch (Exception e) {
                // TODO log incoherence
            }
        });

        user.removeJob(job);
        userDataManager.deleteJobAndSaveUser(user, job);
    }

    @Override
    public DomainPage<EnrichedJob> getUserJobs(UserId userId, int page, int size, JobStatus status, boolean filterLate, String sort) {
        Optional<User> foundUser = userDataManager.findById(userId);
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        List<DomainSpecification> specs = new ArrayList<>(List.of(DomainSpecification.Eq("userId", user.getId(), UserId.class)));

        DomainPage<Job> jobs;
        if(filterLate) {
            // threshold instant : jobs not updated since that instant are considered late
            Instant nowMinusReminderDays = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
            specs.add(DomainSpecification.In("status", JobStatus.activeStatuses()));
            specs.add(DomainSpecification.Lt("statusUpdatedAt", nowMinusReminderDays));
        }
        else {
            if( status != null) {
                specs.add(DomainSpecification.Eq("status", status, JobStatus.class));
            }
        }
        var finalSpecs = DomainSpecification.And(specs);
        if(sort != null && !sort.isEmpty()) {
            DomainSpecification.applySort(finalSpecs, DomainSpecification.Sort(sort));
        }

        jobs = jobDataManager.findPaginated(finalSpecs, page, size);
        return jobEnricher.enrich(jobs, user);
    }

    @Override
    public Job updateJob(UpdateJobCommand command) {
        Optional<User> foundUser = userDataManager.findById(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = foundUser.get();

        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();
        command = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "salary"));

        // FIXME This is not very DDD : the application service (ie this use case) should not check itself if
        // if a job with the same url already exists for the user as it is a business rule and should be in the domain
        // if user wants to update the job's url, we have to check if it does not exist yet
        // it seems that to do things right the user aggregate should handle the job update after all
        // because otherwise the job aggregate cannot check other jobs
        if(!command.url().equals(job.getUrl())) {
            Optional<Job> otherJob = jobDataManager.findByUrlAndUserId(command.url(), user.getId());
            if(otherJob.isPresent() && !otherJob.get().getId().equals(job.getId())) {
                throw new JobAlreadyExistsException();
            }
        }
        job = job.updateJob(command.url(), command.title(), command.company(), command.description(), command.profile(), command.salary());

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the root aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        userDataManager.saveUserAndJob(user, job);
        return job;
    }

    @Override
    public Activity addActivityToJob(CreateActivityCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();
        Activity activity = Activity.builder()
                .type(command.activityType())
                .comment(command.comment())
                .build();

        job = job.addActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        this.jobDataManager.saveJobAndActivity(job, activity);
        return activity;
    }

    @Override
    public Activity updateActivity(UpdateActivityCommand command) {
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        Activity activity = Activity.builder()
                .id(command.activityId())
                .type(command.activityType())
                .comment(command.comment())
                .build();

        job = job.updateActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        this.jobDataManager.saveJobAndActivity(job, activity);
        return activity;
    }

    @Override
    public Job getUserJob(UserId userId, JobId jobId) {
        return jobDataManager.findByIdAndUserId(jobId, userId).orElseThrow(JobNotFoundException::new);
    }

    private <T> T sanitizeCommandFields(T command, List<String> fieldsToSanitize) {
        Class<?> clazz = command.getClass();

        Object builder;
        try {
            // get a builder
            Class<?> builderClass = Class.forName(clazz.getName()+"$Builder");
            builder = builderClass.getConstructor(clazz).newInstance(command);

            for (String field : fieldsToSanitize) {
                Method getterMethod = clazz.getMethod(field);
                String fieldValue = (String) getterMethod.invoke(command);

                if (fieldValue != null) {
                    String sanitizedValue = htmlSanitizer.sanitize(fieldValue);
                    Method setterMethod = builder.getClass().getMethod(field, String.class);
                    setterMethod.invoke(builder, sanitizedValue);
                }
            }
            return (T) builder.getClass().getMethod("build").invoke(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    @Override
    public Attachment addAttachmentToJob(CreateAttachmentCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        AttachmentId attachmentId = AttachmentId.generate();

        // FIXME : it seems very un-DDD to handle activity creation here
        // the Job aggregate should be the one to do it, although it would be too complicated for us for the time being

        DownloadableFile file = fileStorage.store(command.file(), command.userId().value().toString()+"/"+attachmentId.value().toString(), command.filename());
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .name(command.name())
                .fileId(file.fileId())
                .filename(command.filename())
                .contentType(file.contentType()).build();
        job = job.addAttachment(attachment);

        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_CREATION).comment(attachment.getName()).build();
        job = job.addActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understant DDD, only the aggregate should be explicitely persisted
        // but I just don't how to do it cleanly for now
        jobDataManager.saveJobAndAttachment(job, attachment, activity);
        return attachment;
    }


    @Override
    public DownloadableFile downloadAttachment(DownloadAttachmentCommand command) {
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        Attachment attachment = job.getAttachments().stream().filter(a -> a.getId().value().toString().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        return fileStorage.retrieve(attachment.getFileId(), attachment.getFilename());
    }

    @Override
    public void deleteAttachment(DeleteAttachmentCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Attachment attachment = foundJob.get().getAttachments().stream().filter(a -> a.getId().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        // FIXME : the activity should be created by the Job aggregate
        // however for now we do it here,
        // to be able to explicitly ask the JobDataManager to delete the attachment and store both the job and the new activity
        Job job = foundJob.get().removeAttachment(attachment);
        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_DELETION).comment(attachment.getName()).build();
        job = job.addActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the root aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        jobDataManager.deleteAttachmentAndSaveJob(job, attachment, activity);
        try {
            fileStorage.delete(attachment.getFileId());
        }
        catch (Exception e) {
            // TODO do something
        }
    }

    @Override
    public Job updateJobStatus(UpdateJobStatusCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get().updateStatus(command.status());
        jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
        return job;
    }

    @Override
    public Job updateJobRating(UpdateJobRatingCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get().updateRating(command.rating());
        jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
        return job;
    }
}
