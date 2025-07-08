package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivitiesCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentsCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentNotFoundException;
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.DownloadAttachmentUseCase;
import com.wilzwert.myjobs.core.domain.model.attachment.ports.driving.GetAttachmentFileInfoUseCase;
import com.wilzwert.myjobs.core.domain.model.job.*;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.*;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
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
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
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

    private final TransactionProvider transactionProvider;

    private final IntegrationEventPublisher integrationEventPublisher;

    private final JobDataManager jobDataManager;

    private final UserDataManager userDataManager;

    private final FileStorage fileStorage;

    private final HtmlSanitizer htmlSanitizer;

    private final JobEnricher jobEnricher = new JobEnricher();

    public JobUseCaseImpl(
            TransactionProvider transactionProvider,
            IntegrationEventPublisher integrationEventPublisher,
            JobDataManager jobDataManager,
            UserDataManager userDataManager,
            FileStorage fileStorage,
            HtmlSanitizer htmlSanitizer) {
        this.transactionProvider = transactionProvider;
        this.integrationEventPublisher = integrationEventPublisher;
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

        CreateJobCommand actualCommand = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "comment", "salary"));

        return transactionProvider.executeInTransaction(() -> {
            Job jobToCreate = Job.create(
                    Job.builder()
                            .url(actualCommand.url())
                            .title(actualCommand.title())
                            .company(actualCommand.company())
                            .description(actualCommand.description())
                            .profile(actualCommand.profile())
                            .comment(actualCommand.comment())
                            .salary(actualCommand.salary())
                            .userId(user.get().getId())
            );
            User updatedUser = user.get().addJob(jobToCreate);

            Job job = updatedUser.getJobByUrl(jobToCreate.getUrl()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));
            userDataManager.saveUserAndJob(updatedUser, job);
            integrationEventPublisher.publish(new JobCreatedEvent(IntegrationEventId.generate(), job.getId()));
            return job;
        });
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

        transactionProvider.executeInTransaction(() -> {
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
            integrationEventPublisher.publish(new JobDeletedEvent(IntegrationEventId.generate(), job.getId()));
            return null;
        });


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

        String statusField = "status";

        if(statusMeta != null) {
            // threshold instant : jobs not updated since that instant are considered late
            switch (statusMeta) {
                case ACTIVE: specs.add(DomainSpecification.in(statusField, JobStatus.activeStatuses())); break;
                case INACTIVE: specs.add(DomainSpecification.in(statusField, JobStatus.inactiveStatuses())); break;
                case LATE:
                    Instant nowMinusReminderDays = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
                    specs.add(DomainSpecification.in(statusField, JobStatus.activeStatuses()));
                    specs.add(DomainSpecification.lt("statusUpdatedAt", nowMinusReminderDays));
                    break;
            }
        }

        if( status != null) {
            specs.add(DomainSpecification.eq(statusField, status, JobStatus.class));
        }

        var finalSpecs = DomainSpecification.and(specs);
        if(sort != null && !sort.isEmpty()) {
            DomainSpecification.applySort(finalSpecs, DomainSpecification.sort(sort));
        }

        jobs = jobDataManager.findPaginated(finalSpecs, page, size);
        return jobEnricher.enrich(jobs, user);
    }

    @Override
    public Job updateJobField(UpdateJobFieldCommand command) {
        User user = userDataManager.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), user.getId()).orElseThrow(JobNotFoundException::new);
        UpdateJobFieldCommand actualCommand = sanitizeCommandFields(command, List.of("value"));

        return transactionProvider.executeInTransaction(() -> {
            User updatedUser = user.updateJobField(job, actualCommand.field(), actualCommand.value());
            // soft reload the updatedJob in the loaded collection
            Job updatedJob = updatedUser.getJobById(job.getId()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));

            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the root aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            userDataManager.saveUserAndJob(updatedUser, updatedJob);

            integrationEventPublisher.publish(new JobFieldUpdatedEvent(IntegrationEventId.generate(), job.getId(), actualCommand.field()));
            return updatedJob;
        });
    }

    @Override
    public Job updateJob(UpdateJobFullCommand command) {
        User user = userDataManager.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), user.getId()).orElseThrow(JobNotFoundException::new);

        UpdateJobFullCommand actualCommand = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "comment", "salary"));

        return transactionProvider.executeInTransaction(() -> {

            User updatedUser = user.updateJob(job, actualCommand.url(), actualCommand.title(), actualCommand.company(), actualCommand.description(), actualCommand.profile(), actualCommand.comment(), actualCommand.salary());
            // soft reload the updatedJob in the loaded collection
            Job updatedJob = updatedUser.getJobById(job.getId()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));

            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the root aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            userDataManager.saveUserAndJob(updatedUser, updatedJob);
            integrationEventPublisher.publish(new JobUpdatedEvent(IntegrationEventId.generate(), job.getId()));
            return updatedJob;
        });
    }

    @Override
    public List<Activity> addActivitiesToJob(CreateActivitiesCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        Job job = foundJob.get();

        List<CreateActivityCommand> createActivityCommandList = command.createActivityCommandList();
        List<CreateActivityCommand> actualCommands = createActivityCommandList.stream()
                .map(c -> sanitizeCommandFields(c, List.of("comment")))
                .toList();

        CreateActivitiesCommand actualCommand = new CreateActivitiesCommand.Builder(command).commandList(actualCommands).build();

        return transactionProvider.executeInTransaction(() -> {
            List<Activity> addedActivities = new ArrayList<>();
            Job updatedJob = job;

            for(CreateActivityCommand createActivityCommand : actualCommand.createActivityCommandList()) {
                Activity activity = Activity.builder()
                        .type(createActivityCommand.activityType())
                        .comment(createActivityCommand.comment())
                        .build();
                updatedJob = updatedJob.addActivity(activity);

                // FIXME
                // this is an ugly workaround to force the infra (persistence in particular) to save all data
                // as I understand DDD, only the aggregate should be explicitly persisted
                // but I just don't how to do it cleanly for now
                this.jobDataManager.saveJobAndActivity(updatedJob, activity);

                this.integrationEventPublisher.publish(new ActivityCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));
                addedActivities.add(activity);
            }

            return addedActivities;
        });
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

    /**
     * FIXME : this should be improved to avoid reflection and ugly casts, and externalized
     * @param command the command to sanitize
     * @param fieldsToSanitize the command fields to sanitize
     * @return a new command of the same class
     * @param <T> the command class
     */
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
    public List<Attachment> addAttachmentsToJob(CreateAttachmentsCommand command) {
        Job job = jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        return transactionProvider.executeInTransaction(() -> {
            List<Attachment> addedAttachments = new ArrayList<>();
            Job updatedJob = job;
            for(CreateAttachmentCommand createAttachmentCommand: command.createAttachmentCommandList()) {
                AttachmentId attachmentId = AttachmentId.generate();
                DownloadableFile file = fileStorage.store(createAttachmentCommand.file(), command.userId().value().toString() + "/" + attachmentId.value().toString(), createAttachmentCommand.filename());
                Attachment attachment = Attachment.builder()
                        .id(attachmentId)
                        .name(createAttachmentCommand.name())
                        .fileId(file.fileId())
                        .filename(createAttachmentCommand.filename())
                        .contentType(file.contentType()).build();
                updatedJob = updatedJob.addAttachment(attachment);

                Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_CREATION).comment(attachment.getName()).build();
                updatedJob = updatedJob.addActivity(activity);

                // FIXME
                // this is an ugly workaround to force the infra (persistence in particular) to save all data
                // as I understand DDD, only the aggregate should be explicitly persisted
                // but I just don't how to do it cleanly for now
                jobDataManager.saveJobAndAttachment(updatedJob, attachment, activity);
                integrationEventPublisher.publish(new AttachmentCreatedEvent(IntegrationEventId.generate(), job.getId(), attachment.getId()));
                integrationEventPublisher.publish(new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));

                addedAttachments.add(attachment);
            }

            return addedAttachments;
        });
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
        transactionProvider.executeInTransaction(() -> {
            // FIXME : maybe the activity should be created by the Job aggregate
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
            } catch (Exception e) {
                // TODO do something about it
            }
            finally {
                integrationEventPublisher.publish(new AttachmentDeletedEvent(IntegrationEventId.generate(), job.getId(), attachment.getId()));
                integrationEventPublisher.publish(new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));
            }
            return null;
        });
    }

    @Override
    public Job updateJobStatus(UpdateJobStatusCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        return transactionProvider.executeInTransaction(() -> {
            Job job = foundJob.get().updateStatus(command.status());
            jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
            integrationEventPublisher.publish(new JobStatusUpdatedEvent(IntegrationEventId.generate(), job.getId(), job.getStatus()));
            return job;
        });
    }

    @Override
    public Job updateJobRating(UpdateJobRatingCommand command) {
        Optional<Job> foundJob = jobDataManager.findByIdAndUserId(command.jobId(), command.userId());
        if(foundJob.isEmpty()) {
            throw new JobNotFoundException();
        }

        return transactionProvider.executeInTransaction(() -> {
            Job job = foundJob.get().updateRating(command.rating());
            jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
            integrationEventPublisher.publish(new JobRatingUpdatedEvent(IntegrationEventId.generate(), job.getId(), job.getRating()));
            return job;
        });
    }
}