package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.*;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 */
public class JobUseCaseImpl implements
    CreateJobUseCase,
    GetUserJobUseCase,
    UpdateJobUseCase,
    UpdateJobStatusUseCase,
    UpdateJobRatingUseCase,
    DeleteJobUseCase,
    GetUserJobsUseCase,
    AddActivityToJobUseCase,
    UpdateActivityUseCase,
    AddAttachmentToJobUseCase,
    DownloadAttachmentUseCase,
    DeleteAttachmentUseCase,
    GetAttachmentFileInfoUseCase {

    private final static String SLASH = "/";
    private static final Field[] createJobFields = Arrays.stream(CreateJobCommand.class.getDeclaredFields())
        .filter(field -> !field.getName().equals("userId"))
        .toList()
        .toArray(Field[]::new);

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
    public Job createJob(final CreateJobCommand command) {
        final User user = this.userDataManager.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        final CreateJobCommand actualCommand = this.sanitizeCommandFields(
            command,
            createJobFields
        );

        return this.transactionProvider.executeInTransaction(() -> {
            final Job jobToCreate = Job.create(
                Job.builder()
                    .url(actualCommand.url())
                    .title(actualCommand.title())
                    .company(actualCommand.company())
                    .description(actualCommand.description())
                    .profile(actualCommand.profile())
                    .comment(actualCommand.comment())
                    .salary(actualCommand.salary())
                    .userId(user.getId())
            );
            final User updatedUser = user.addJob(jobToCreate);
            final Job job = updatedUser.getJobByUrl(jobToCreate.getUrl())
                .orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));

            this.userDataManager.saveUserAndJob(updatedUser, job);
            this.integrationEventPublisher.publish(new JobCreatedEvent(IntegrationEventId.generate(), job.getId()));

            return job;
        });
    }

    @Override
    public void deleteJob(final DeleteJobCommand command) {
        final User user = userDataManager.findById(command.userId())
            .orElseThrow(UserNotFoundException::new);

        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), user.getId())
            .orElseThrow(JobNotFoundException::new);

        this.transactionProvider.executeInTransaction(() -> {
            // delete attachments' files
            job.getAttachments().forEach(attachment -> {
                try {
                    this.fileStorage.delete(attachment.getFileId());
                } catch (Exception e) {
                    // TODO log incoherence
                }
            });
            final User updatedUser = user.removeJob(job);
            this.userDataManager.deleteJobAndSaveUser(updatedUser, job);
            this.integrationEventPublisher.publish(new JobDeletedEvent(IntegrationEventId.generate(), job.getId()));
            return null;
        });
    }

    @Override
    public DomainPage<EnrichedJob> getUserJobs(
        final UserId userId,
        final int page,
        final int size,
        final JobStatus status,
        final JobStatusMeta statusMeta,
        final String sort) {

        final User user = this.userDataManager.findById(userId).orElseThrow(UserNotFoundException::new);
        final List<DomainSpecification> specs = new ArrayList<>(
            List.of(DomainSpecification.eq(DomainSpecification.USER_ID_FIELD, user.getId(), UserId.class))
        );

        if (statusMeta != null) {
            // threshold instant: jobs not updated since that instant is considered late
            switch (statusMeta) {
                case ACTIVE ->
                    specs.add(DomainSpecification.in(DomainSpecification.STATUS_FIELD, JobStatus.activeStatuses()));
                case INACTIVE ->
                    specs.add(DomainSpecification.in(DomainSpecification.STATUS_FIELD, JobStatus.inactiveStatuses()));
                case LATE -> {
                    final Instant nowMinusReminderDays = Instant.now().minus(user.getJobFollowUpReminderDays(), ChronoUnit.DAYS);
                    specs.add(DomainSpecification.in(DomainSpecification.STATUS_FIELD, JobStatus.activeStatuses()));
                    specs.add(DomainSpecification.lt(DomainSpecification.STATUS_UPDATED_AT_FIELD, nowMinusReminderDays));
                }
            }
        }

        if (status != null) {
            specs.add(DomainSpecification.eq(DomainSpecification.STATUS_FIELD, status, JobStatus.class));
        }

        final var finalSpecs = DomainSpecification.and(specs);
        if (sort != null && !sort.isEmpty()) { // Avec spring StringUtils.hasText(sort)
            DomainSpecification.applySort(finalSpecs, DomainSpecification.sort(sort));
        }

        final DomainPage<Job> jobs = this.jobDataManager.findPaginated(finalSpecs, page, size);

        return this.jobEnricher.enrich(jobs, user);
    }

    @Override
    public Job updateJobField(final UpdateJobFieldCommand command) {
        final User user = this.userDataManager.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), user.getId()).orElseThrow(JobNotFoundException::new);
        final UpdateJobFieldCommand actualCommand = sanitizeCommandFields(command, List.of("value"));

        return this.transactionProvider.executeInTransaction(() -> {
            final User updatedUser = user.updateJobField(job, actualCommand.field(), actualCommand.value());
            // soft reload the updatedJob in the loaded collection
            final Job updatedJob = updatedUser.getJobById(job.getId()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));
            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the root aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            this.userDataManager.saveUserAndJob(updatedUser, updatedJob);

            this.integrationEventPublisher.publish(new JobFieldUpdatedEvent(IntegrationEventId.generate(), job.getId(), actualCommand.field()));
            return updatedJob;
        });
    }

    @Override
    public Job updateJob(final UpdateJobFullCommand command) {
        final User user = this.userDataManager.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), user.getId()).orElseThrow(JobNotFoundException::new);
        final UpdateJobFullCommand actualCommand = sanitizeCommandFields(command, List.of("title", "company", "description", "profile", "comment", "salary"));

        return this.transactionProvider.executeInTransaction(() -> {
            final User updatedUser = user.updateJob(job, actualCommand.url(), actualCommand.title(), actualCommand.company(), actualCommand.description(), actualCommand.profile(), actualCommand.comment(), actualCommand.salary());
            // soft reload the updatedJob in the loaded collection
            final Job updatedJob = updatedUser.getJobById(job.getId()).orElseThrow(() -> new DomainException(ErrorCode.UNEXPECTED_ERROR));
            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the root aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            this.userDataManager.saveUserAndJob(updatedUser, updatedJob);
            this.integrationEventPublisher.publish(new JobUpdatedEvent(IntegrationEventId.generate(), job.getId()));
            return updatedJob;
        });
    }

    @Override
    public Activity addActivityToJob(final CreateActivityCommand command) {
        final Job job = jobDataManager.findByIdAndUserId(command.jobId(), command.userId())
            .orElseThrow(JobNotFoundException::new);

        final CreateActivityCommand actualCommand = sanitizeCommandFields(command, List.of("comment"));

        return transactionProvider.executeInTransaction(() -> {
            final Activity activity = Activity.builder()
                .type(actualCommand.activityType())
                .comment(actualCommand.comment())
                .build();

            final Job updatedJob = job.addActivity(activity);
            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            this.jobDataManager.saveJobAndActivity(updatedJob, activity);

            this.integrationEventPublisher.publish(new ActivityCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));
            return activity;
        });
    }

    @Override
    public Activity updateActivity(final UpdateActivityCommand command) {
        Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        final Activity activity = Activity.builder()
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
    public Job getUserJob(final UserId userId, final JobId jobId) {
        return this.jobDataManager.findByIdAndUserId(jobId, userId).orElseThrow(JobNotFoundException::new);
    }

    /**
     * FIXME : this should be improved to avoid reflection and ugly casts, and externalized
     *
     * @param command          the command to sanitize
     * @param fieldsToSanitize the command fields to sanitize
     * @param <T>              the command class
     * @return a new comment of the same class
     */
    private <T> T sanitizeCommandFields(final T command, final List<String> fieldsToSanitize) {
        Class<?> clazz = command.getClass();
        Object builder;
        try {
            // get a builder
            Class<?> builderClass = Class.forName(clazz.getName() + "$Builder");
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

    /**
     * Pas ouf comme alternative. Le cast t'as pas le choix pour l'instant.
     *
     * @param command          the command to sanitize
     * @param fieldsToSanitize the command fields to sanitize
     * @param <T>              the command class
     * @return the command fields to sanitize
     */
    private <T> T sanitizeCommandFields(final T command, final Field[] fieldsToSanitize) {
        final Class<?> clazz = command.getClass();
        try {
            final Class<?> builderClass = Class.forName(clazz.getName() + "$Builder");
            final Object builder = builderClass.getConstructor(clazz).newInstance(command);

            for (Field field : fieldsToSanitize) {
                field.setAccessible(true);
                final Object rawValue = field.get(command);
                final String sanitizedValue = this.htmlSanitizer.sanitize((String) rawValue);
                final Field builderField = builder.getClass().getDeclaredField(field.getName());
                builderField.setAccessible(true);
                builderField.set(builder, sanitizedValue);
            }

            return (T) builder.getClass().getMethod("build").invoke(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return command;
    }

    private String capitalize(final String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    @Override
    public Attachment addAttachmentToJob(final CreateAttachmentCommand command) {
        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId())
            .orElseThrow(JobNotFoundException::new);

        final AttachmentId attachmentId = AttachmentId.generate();

        return transactionProvider.executeInTransaction(() -> {
            final DownloadableFile file = fileStorage.store(command.file(), command.userId().value().toString() + SLASH + attachmentId.value().toString(), command.filename());
            final Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .name(command.name())
                .fileId(file.fileId())
                .filename(command.filename())
                .contentType(file.contentType()).build();

            Job updatedJob = job.addAttachment(attachment);

            final Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_CREATION).comment(attachment.getName()).build();
            updatedJob = updatedJob.addActivity(activity);
            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            this.jobDataManager.saveJobAndAttachment(updatedJob, attachment, activity);
            this.integrationEventPublisher.publish(new AttachmentCreatedEvent(IntegrationEventId.generate(), job.getId(), attachment.getId()));
            this.integrationEventPublisher.publish(new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));

            return attachment;
        });
    }


    @Override
    public DownloadableFile downloadAttachment(final DownloadAttachmentCommand command) {
        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        final Attachment attachment = job.getAttachments().stream()
            .filter(a -> a.getId().value().toString().equals(command.id()))
            .findAny()
            .orElseThrow(AttachmentNotFoundException::new);

        return this.fileStorage.retrieve(attachment.getFileId(), attachment.getFilename());
    }

    @Override
    public AttachmentFileInfo getAttachmentFileInfo(final DownloadAttachmentCommand command) {
        final Job job = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId()).orElseThrow(JobNotFoundException::new);

        final Attachment attachment = job.getAttachments().stream()
            .filter(a -> a.getId().value().toString().equals(command.id()))
            .findAny()
            .orElseThrow(AttachmentNotFoundException::new);

        return new AttachmentFileInfo(attachment.getFileId(), this.fileStorage.generateProtectedUrl(job.getId(), attachment.getId(), attachment.getFileId()));
    }

    @Override
    public void deleteAttachment(final DeleteAttachmentCommand command) {
        final Job foundJob = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId())
            .orElseThrow(JobNotFoundException::new);

        final Attachment attachment = foundJob.getAttachments().stream()
            .filter(a -> a.getId().equals(command.id()))
            .findAny()
            .orElseThrow(AttachmentNotFoundException::new);

        this.transactionProvider.executeInTransaction(() -> {
            // FIXME : maybe the activity should be created by the Job aggregate
            // however for now we do it here,
            // to be able to explicitly ask the JobDataManager to delete the attachment and store both the job and the new activity
            Job job = foundJob.removeAttachment(attachment);
            final Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_DELETION).comment(attachment.getName()).build();
            job = job.addActivity(activity);
            // FIXME
            // this is an ugly workaround to force the infra (persistence in particular) to save all data
            // as I understand DDD, only the root aggregate should be explicitly persisted
            // but I just don't how to do it cleanly for now
            this.jobDataManager.deleteAttachmentAndSaveJob(job, attachment, activity);
            try {
                this.fileStorage.delete(attachment.getFileId());
            } catch (Exception e) {
                // TODO do something about it
            } finally {
                this.integrationEventPublisher.publish(new AttachmentDeletedEvent(IntegrationEventId.generate(), job.getId(), attachment.getId()));
                this.integrationEventPublisher.publish(new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), job.getId(), activity.getId(), activity.getType()));
            }
            return null;
        });
    }

    @Override
    public Job updateJobStatus(final UpdateJobStatusCommand command) {
        final Job foundJob = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId())
            .orElseThrow(JobNotFoundException::new);

        return this.transactionProvider.executeInTransaction(() -> {
            final Job job = foundJob.updateStatus(command.status());
            this.jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
            this.integrationEventPublisher.publish(new JobStatusUpdatedEvent(IntegrationEventId.generate(), job.getId(), job.getStatus()));

            return job;
        });
    }

    @Override
    public Job updateJobRating(final UpdateJobRatingCommand command) {
        final Job foundJob = this.jobDataManager.findByIdAndUserId(command.jobId(), command.userId())
            .orElseThrow(JobNotFoundException::new);

        return this.transactionProvider.executeInTransaction(() -> {
            final Job job = foundJob.updateRating(command.rating());
            this.jobDataManager.saveJobAndActivity(job, job.getActivities().getFirst());
            this.integrationEventPublisher.publish(new JobRatingUpdatedEvent(IntegrationEventId.generate(), job.getId(), job.getRating()));

            return job;
        });
    }
}