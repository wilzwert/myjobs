package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.*;
import com.wilzwert.myjobs.core.domain.exception.*;
import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.*;

import java.lang.reflect.Method;
import java.time.Instant;
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
        Job job = user.get().addJob(Job.create(command.url(), command.title(), command.company(), command.description(), command.profile(), user.get().getId()));
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
    public DomainPage<Job> getUserJobs(UserId userId, int page, int size, JobStatus status) {
        Optional<User> user = userService.findById(userId);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return jobService.findAllByUserId(user.get().getId(), page, size, status);
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
        command = sanitizeCommandFields(command, List.of("title", "url", "company", "description", "profile"));

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
        job = job.updateJob(command.url(), command.title(), command.company(), command.description(), command.profile());

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
        Activity activity = new Activity(ActivityId.generate(), command.activityType(), job.getId(), command.comment(), Instant.now(), Instant.now());

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

    private String capitalize(String field) {
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private <T> T sanitizeCommandFields(T command, List<String> fieldsToSanitize) {
        Class<?> clazz = command.getClass();
        // UpdateJobCommand.Builder builder = new UpdateJobCommand.Builder((UpdateJobCommand) command);
        Object builder = null;
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
        Attachment attachment = new Attachment(attachmentId, job.getId(), command.name(), file.path(), command.filename(), file.contentType(), Instant.now(), Instant.now());
        job = job.addAttachment(attachment);

        Activity activity = new Activity(ActivityId.generate(), ActivityType.ATTACHMENT_CREATION, job.getId(), attachment.getName(), Instant.now(), Instant.now());
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
        Optional<Job> foundJob = jobService.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Attachment attachment = foundJob.get().getAttachments().stream().filter(a -> a.getId().value().toString().equals(command.id())).findAny().orElse(null);
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

        Attachment attachment = foundJob.get().getAttachments().stream().filter(a -> a.getId().value().toString().equals(command.id())).findAny().orElse(null);
        if(attachment == null) {
            throw new AttachmentNotFoundException();
        }

        // FIXME : the activity should be created by the Job aggregate
        // however for now we do it here,
        // to be able to explicitly ask the JobService to delete the attachment and store both the job and the new activity
        Job job = foundJob.get().removeAttachment(attachment);
        Activity activity = new Activity(ActivityId.generate(), ActivityType.ATTACHMENT_DELETION, job.getId(), attachment.getName(), Instant.now(), Instant.now());
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

        System.out.println("Oui?");

        Job job = foundJob.get().updateRating(command.rating());
        jobService.saveJobAndActivity(job, job.getActivities().getFirst());
        return job;
    }
}
