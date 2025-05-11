package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.*;
import com.wilzwert.myjobs.core.domain.exception.*;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;
import com.wilzwert.myjobs.core.domain.shared.criteria.DomainCriteria;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:16:55
 */

public class JobUseCaseImpl implements CreateJobUseCase, GetUserJobUseCase, UpdateJobUseCase, UpdateJobStatusUseCase, UpdateJobRatingUseCase, DeleteJobUseCase, GetUserJobsUseCase, AddActivityToJobUseCase, AddAttachmentToJobUseCase, DownloadAttachmentUseCase, DeleteAttachmentUseCase {

    private final JobService jobService;

    private final UserService userService;

    private final FileStorage fileStorage;

    private final HtmlSanitizer htmlSanitizer;

    public JobUseCaseImpl(JobService jobService, UserService userService, FileStorage fileStorage, HtmlSanitizer htmlSanitizer) {
        this.jobService = jobService;
        this.userService = userService;
        this.fileStorage = fileStorage;
        this.htmlSanitizer = htmlSanitizer;
    }

    @Override
    public Job createJob(CreateJobCommand command) {
        Optional<User> user = userService.findByIdWithJobs(command.userId());
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        Job jobToCreate = Job.builder()
                .url(command.url())
                .title(command.title())
                .company(command.company())
                .description(command.description())
                .profile(command.profile())
                .salary(command.salary())
                .userId(user.get().getId())
                .build();
        Job job = user.get().addJob(jobToCreate);
        userService.saveUserAndJob(user.get(), job);
        return job;
    }

    @Override
    public void deleteJob(DeleteJobCommand command) {
        Optional<User> foundUser = userService.findByIdWithJobs(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), user.getId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        job.getAttachments().forEach(attachment -> {
            job.removeAttachment(attachment);
            jobService.deleteAttachment(job, attachment, null);
            try {
                fileStorage.delete(attachment.getFileId());
            }
            catch (Exception e) {
                System.out.println("failed to delete attachment "+attachment.getFileId()+e.getMessage());
                // TODO log incoherence
            }
        });

        user.removeJob(job);
        userService.deleteJobAndSaveUser(user, job);
    }

    @Override
    public DomainPage<Job> getUserJobs(UserId userId, int page, int size, JobStatus status, boolean filterLate, String sort) {
        Optional<User> foundUser = userService.findById(userId);
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = foundUser.get();

        if(filterLate) {
            // threshold instant : jobs not updated since that instant are considered late
            Instant nowMinusReminderDays = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
            List<DomainCriteria> criteriaList = List.of(
                new DomainCriteria.In<>("status", JobStatus.activeStatuses()),
                new DomainCriteria.Lt<>("status_updated_at", nowMinusReminderDays)
            );
            return jobService.findByUserWithCriteriaPaginated(user, criteriaList, page, size, sort);
        }
        return jobService.findAllByUserIdPaginated(user.getId(), page, size, status, sort);
    }

    @Override
    public Job updateJob(UpdateJobCommand command) {
        Optional<User> foundUser = userService.findById(command.userId());
        if(foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = foundUser.get();

        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), user.getId());
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
            Optional<Job> otherJob = jobService.findByUrlAndUserId(command.url(), user.getId());
            if(otherJob.isPresent() && !otherJob.get().getId().equals(job.getId())) {
                throw new JobAlreadyExistsException();
            }
        }
        job = job.updateJob(command.url(), command.title(), command.company(), command.description(), command.profile(), command.salary());

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the root aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        userService.saveUserAndJob(user, job);
        return job;
    }

    @Override
    public Activity addActivityToJob(CreateActivityCommand command) {
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
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
        this.jobService.saveJobAndActivity(job, activity);
        return activity;
    }

    @Override
    public Job getUserJob(UserId userId, JobId jobId) {
        return jobService.findByIdAndUserId(jobId, userId).orElseThrow(JobNotFoundException::new);
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
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        AttachmentId attachmentId = AttachmentId.generate();

        // FIXME : it seems very un-DDD to handle activity creation here
        // the Job aggregate should be the one to do it, although it would be too complicated for us for the time being

        DownloadableFile file = fileStorage.store(command.file(), command.userId().value().toString()+"/"+attachmentId.value().toString(), command.filename());
        Attachment attachment = Attachment.builder().id(attachmentId).name(command.name()).fileId(file.path()).filename(command.filename()).contentType(file.contentType()).build();
        job = job.addAttachment(attachment);

        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_CREATION).comment(attachment.getName()).build();
        job = job.addActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understant DDD, only the aggregate should be explicitely persisted
        // but I just don't how to do it cleanly for now
        jobService.saveJobAndAttachment(job, attachment, activity);
        return attachment;
    }


    @Override
    public DownloadableFile downloadAttachment(DownloadAttachmentCommand command) {
        Job job = jobService.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        Attachment attachment = job.getAttachments().stream().filter(a -> a.getId().value().toString().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        return fileStorage.retrieve(attachment.getFileId(), attachment.getFilename());
    }

    @Override
    public void deleteAttachment(DeleteAttachmentCommand command) {
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Attachment attachment = foundJob.get().getAttachments().stream().filter(a -> a.getId().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        // FIXME : the activity should be created by the Job aggregate
        // however for now we do it here,
        // to be able to explicitly ask the JobService to delete the attachment and store both the job and the new activity
        Job job = foundJob.get().removeAttachment(attachment);
        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_DELETION).comment(attachment.getName()).build();
        job = job.addActivity(activity);

        // FIXME
        // this is an ugly workaround to force the infra (persistence in particular) to save all data
        // as I understand DDD, only the aggregate should be explicitly persisted
        // but I just don't how to do it cleanly for now
        jobService.deleteAttachment(job, attachment, activity);
        try {
            fileStorage.delete(attachment.getFileId());
        }
        catch (Exception e) {
            // TODO log incoherence
        }
    }

    @Override
    public Job updateJobStatus(UpdateJobStatusCommand command) {
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get().updateStatus(command.status());
        jobService.saveJobAndActivity(job, job.getActivities().getFirst());
        return job;
    }

    @Override
    public Job updateJobRating(UpdateJobRatingCommand command) {
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get().updateRating(command.rating());
        jobService.saveJobAndActivity(job, job.getActivities().getFirst());
        return job;
    }
}
