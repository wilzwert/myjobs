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
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.GetAttachmentFileInfoUseCase;
import com.wilzwert.myjobs.core.domain.model.job.*;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.*;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
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
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobUseCaseImpl implements CreateJobUseCase, GetUserJobUseCase, UpdateJobUseCase, UpdateJobStatusUseCase, UpdateJobRatingUseCase, DeleteJobUseCase, GetUserJobsUseCase, AddActivityToJobUseCase, UpdateActivityUseCase, AddAttachmentToJobUseCase, DownloadAttachmentUseCase, DeleteAttachmentUseCase, GetAttachmentFileInfoUseCase {

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

        command = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "comment", "salary"));

        Job jobToCreate = Job.create(
                Job.builder()
                .url(command.url())
                .title(command.title())
                .company(command.company())
                .description(command.description())
                .profile(command.profile())
                .comment(command.comment())
                .salary(command.salary())
                .userId(user.get().getId())
        );

        User updatedUser = user.get().addJob(jobToCreate);
        Job job = updatedUser.getJobByUrl(jobToCreate.getUrl()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));
        userDataManager.saveUserAndJob(updatedUser, job);
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

        User updatedUser = user.removeJob(job);
        userDataManager.deleteJobAndSaveUser(updatedUser, job);
    }

    @Override
    public DomainPage<EnrichedJob> getUserJobs(UserId userId, int page, int size, JobStatus status, JobStatusMeta statusMeta, String sort) {
        Optional<User> foundUser = userDataManager.findById(userId);
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        List<DomainSpecification> specs = new ArrayList<>(List.of(DomainSpecification.eq("userId", user.getId(), UserId.class)));

        DomainPage<Job> jobs;

        if(statusMeta != null) {
            // threshold instant : jobs not updated since that instant are considered late
            switch (statusMeta) {
                case ACTIVE: specs.add(DomainSpecification.in("status", JobStatus.activeStatuses())); break;
                case INACTIVE: specs.add(DomainSpecification.in("status", JobStatus.inactiveStatuses())); break;
                case LATE:
                    Instant nowMinusReminderDays = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
                    specs.add(DomainSpecification.in("status", JobStatus.activeStatuses()));
                    specs.add(DomainSpecification.lt("statusUpdatedAt", nowMinusReminderDays));
                    break;
            }
        }

        if( status != null) {
            specs.add(DomainSpecification.eq("status", status, JobStatus.class));
        }

        var finalSpecs = DomainSpecification.and(specs);
        if(sort != null && !sort.isEmpty()) {
            DomainSpecification.applySort(finalSpecs, DomainSpecification.sort(sort));
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
        command = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "comment", "salary"));

        User updatedUser = user.updateJob(job, command.url(), command.title(), command.company(), command.description(), command.profile(), command.comment(), command.salary());
        // soft reload the updatedJob in the loaded collection
        Job updatedJob = updatedUser.getJobById(job.getId()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the root aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        userDataManager.saveUserAndJob(updatedUser, updatedJob);
        return updatedJob;
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
    public AttachmentFileInfo getAttachmentFileInfo(DownloadAttachmentCommand command) {
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        Attachment attachment = job.getAttachments().stream().filter(a -> a.getId().value().toString().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        return new AttachmentFileInfo(attachment.getFileId(), fileStorage.generateProtectedUrl(job.getId(), attachment.getId(), attachment.getFileId()));
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
