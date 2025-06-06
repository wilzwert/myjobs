package com.wilzwert.myjobs.core.domain.model.job;


import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */

class JobTest {

    @Test
    void whenInvalid_thenJobBuildShouldThrowValidationException() {
        var builder = Job.builder();
        ValidationException exception = assertThrows(ValidationException.class, builder::build);
        assertNotNull(exception);
        assertEquals(5, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("userId").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("title").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("company").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("description").getFirst().code());
        assertEquals(ErrorCode.INVALID_URL, exception.getErrors().getErrors().get("url").getFirst().code());
    }

    @Test
    void shouldCreateJobWithDefaultValues() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant before = Instant.now();
        Job job = Job.create(Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .comment("Job comment")
                .salary("TBD")
                .userId(userId)
        );
        Instant after = Instant.now();

        assertNotNull(job);
        assertEquals("Job title", job.getTitle());
        assertEquals("http://www.example.com", job.getUrl());
        assertEquals(JobStatus.CREATED, job.getStatus());
        assertEquals(JobRating.of(0), job.getRating());
        assertEquals("Job company", job.getCompany());
        assertEquals("Job description", job.getDescription());
        assertEquals("Job profile", job.getProfile());
        assertEquals("Job comment", job.getComment());
        assertEquals("TBD", job.getSalary());
        assertEquals(userId, job.getUserId());
        Instant createdAt = job.getCreatedAt();
        Instant updatedAt = job.getUpdatedAt();
        Instant statusUpdatedAt = job.getStatusUpdatedAt();
        assertTrue(createdAt.equals(before) || createdAt.equals(after) || createdAt.isAfter(before) && createdAt.isBefore(after));
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
        assertTrue(statusUpdatedAt.equals(before) || statusUpdatedAt.equals(after) || statusUpdatedAt.isAfter(before) && statusUpdatedAt.isBefore(after));
        assertEquals(Collections.emptyList(), job.getActivities());
        assertEquals(Collections.emptyList(), job.getAttachments());
        assertEquals(userId.value(), job.getUserId().value());
    }

    @Test
    void shouldCreateJob() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();
        Activity activity1 = Activity.builder()
                .id(ActivityId.generate())
                .createdAt(now)
                .updatedAt(now)
                .type(ActivityType.CREATION)
                .comment("Creation")
                .build();

        Activity activity2 = Activity.builder()
                .id(ActivityId.generate())
                .createdAt(now)
                .updatedAt(now)
                .type(ActivityType.APPLICATION)
                .comment("Application")
                .build();
        List<Activity> activities = List.of(activity1, activity2);

        Attachment attachment1 = Attachment.builder()
                .id(AttachmentId.generate())
                .createdAt(now)
                .updatedAt(now)
                .fileId("attachementFile1")
                .name("Attachment 1")
                .filename("attachment1.doc")
                .contentType("application/msword")
                .build();

        Attachment attachment2 = Attachment.builder()
                .id(AttachmentId.generate())
                .createdAt(now)
                .updatedAt(now)
                .fileId("attachementFile2")
                .name("Attachment 2")
                .filename("attachment2.doc")
                .contentType("application/msword")
                .build();

        List<Attachment> attachments = List.of(attachment1, attachment2);

        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.PENDING)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .comment("Job comment")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .statusUpdatedAt(now)
                .activities(activities)
                .attachments(attachments)
                .build();

        assertNotNull(job);
        assertEquals("Job title", job.getTitle());
        assertEquals("http://www.example.com", job.getUrl());
        assertEquals(JobStatus.PENDING, job.getStatus());
        assertEquals(JobRating.of(3), job.getRating());
        assertEquals("Job company", job.getCompany());
        assertEquals("Job description", job.getDescription());
        assertEquals("Job profile", job.getProfile());
        assertEquals("Job comment", job.getComment());
        assertEquals("TBD", job.getSalary());
        assertEquals(userId, job.getUserId());
        assertEquals(now, job.getCreatedAt());
        assertEquals(now, job.getUpdatedAt());
        assertEquals(now, job.getStatusUpdatedAt());
        assertEquals(activities, job.getActivities());
        assertEquals(attachments, job.getAttachments());
        assertEquals(userId.value(), job.getUserId().value());
    }

    @Test
    void shouldAddActivity() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .activities(Collections.emptyList())
                .attachments(Collections.emptyList())
                .build();

        ActivityId activityId = ActivityId.generate();
        Activity activity = Activity.builder()
                .id(activityId)
                .type(ActivityType.APPLICATION)
                .comment("Application")
                .updatedAt(now)
                .createdAt(now)
                .build();
        Instant before = Instant.now();
        Job updatedJob = job.addActivity(activity);
        Instant after = Instant.now();
        assertNotNull(updatedJob);
        assertEquals("Job title", updatedJob.getTitle());
        // status should have changed since we made an application
        assertEquals(JobStatus.PENDING, updatedJob.getStatus());
        assertEquals(JobRating.of(3), updatedJob.getRating());
        assertEquals("Job company", updatedJob.getCompany());
        assertEquals("Job description", updatedJob.getDescription());
        assertEquals("Job profile", updatedJob.getProfile());
        assertEquals("TBD", updatedJob.getSalary());
        assertEquals(userId.value(), updatedJob.getUserId().value());
        assertEquals(now, updatedJob.getCreatedAt());
        Instant updatedAt = updatedJob.getUpdatedAt();
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
        // job status has changed, status updated at should have changed too
        Instant statusUpdatedAt = updatedJob.getStatusUpdatedAt();
        assertTrue(statusUpdatedAt.equals(before) || statusUpdatedAt.equals(after) || statusUpdatedAt.isAfter(before) && statusUpdatedAt.isBefore(after));

        Activity addedActivity = updatedJob.getActivities().getLast();
        assertNotNull(addedActivity);
        assertEquals(activityId, addedActivity.getId());
    }

    @Test
    void shouldAddAttachment() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .comment("Job comment")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .activities(Collections.emptyList())
                .attachments(Collections.emptyList())
                .build();

        AttachmentId attachmentId = AttachmentId.generate();
        Attachment attachment = Attachment.builder()
                .id(attachmentId)
                .name("attachment")
                .fileId("attachment-file")
                .contentType("application/pdf")
                .filename("attachment.pdf")
                .updatedAt(now)
                .createdAt(now)
                .build();
        Instant before = Instant.now();
        Job updatedJob = job.addAttachment(attachment);
        Instant after = Instant.now();
        assertNotNull(updatedJob);
        assertEquals("Job title", updatedJob.getTitle());
        // status should have changed since we made an application
        assertEquals(JobStatus.CREATED, updatedJob.getStatus());
        assertEquals(JobRating.of(3), updatedJob.getRating());
        assertEquals("Job company", updatedJob.getCompany());
        assertEquals("Job description", updatedJob.getDescription());
        assertEquals("Job profile", updatedJob.getProfile());
        assertEquals("Job comment", updatedJob.getComment());
        assertEquals("TBD", updatedJob.getSalary());
        assertEquals(userId.value(), updatedJob.getUserId().value());
        assertEquals(now, updatedJob.getCreatedAt());
        Instant updatedAt = updatedJob.getUpdatedAt();
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));

        Attachment addedAttachment = updatedJob.getAttachments().getLast();
        assertNotNull(addedAttachment);
        assertEquals(attachmentId, addedAttachment.getId());
    }

    @Test
    void shouldRemoveAttachment() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();

        Attachment attachment1 = Attachment.builder()
                .id(AttachmentId.generate())
                .createdAt(now)
                .updatedAt(now)
                .fileId("attachementFile1")
                .name("Attachment 1")
                .filename("attachment1.doc")
                .contentType("application/msword")
                .build();

        Attachment attachment2 = Attachment.builder()
                .id(AttachmentId.generate())
                .createdAt(now)
                .updatedAt(now)
                .fileId("attachementFile2")
                .name("Attachment 2")
                .filename("attachment2.doc")
                .contentType("application/msword")
                .build();

        List<Attachment> attachments = List.of(attachment1, attachment2);

        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.PENDING)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .attachments(attachments)
                .activities(Collections.emptyList())
                .build();

        assertNotNull(job);

        Job updatedJob = job.removeAttachment(attachment1);
        assertNotNull(updatedJob);
        assertEquals(jobId, updatedJob.getId());
        assertEquals(1, updatedJob.getAttachments().size());
        assertFalse(updatedJob.getAttachments().contains(attachment1));

    }

    @Test
    void whenUpdateStatusToSameStatus_thenShouldDoNothing() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant jobCreatedAt = Instant.parse("2025-03-09T13:45:30Z");
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(jobCreatedAt)
                .updatedAt(jobCreatedAt)
                .statusUpdatedAt(jobCreatedAt)
                .build();

        Job updatedJob = job.updateStatus(JobStatus.CREATED);
        assertSame(updatedJob, job);
        assertEquals(job.getStatus(), updatedJob.getStatus());
        assertEquals(jobCreatedAt, updatedJob.getStatusUpdatedAt());
    }

    @Test
    void shouldUpdateStatus() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant jobCreatedAt = Instant.parse("2025-03-09T13:45:30Z");
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(jobCreatedAt)
                .updatedAt(jobCreatedAt)
                .statusUpdatedAt(jobCreatedAt)
                .activities(Collections.emptyList())
                .attachments(Collections.emptyList())
                .build();

        Instant before = Instant.now();
        Job updatedJob = job.updateStatus(JobStatus.PENDING);
        Instant after = Instant.now();

        assertEquals(JobStatus.PENDING, updatedJob.getStatus());
        // an APPLICATION activity should have been added
        assertEquals(1, updatedJob.getActivities().size());
        assertEquals(ActivityType.APPLICATION, updatedJob.getActivities().getFirst().getType());
        // status has changed, status update date must have changed too
        Instant statusUpdatedAt = updatedJob.getStatusUpdatedAt();
        assertTrue(statusUpdatedAt.equals(before) || statusUpdatedAt.equals(after) || statusUpdatedAt.isAfter(before) && statusUpdatedAt.isBefore(after));

    }

    @Test
    void whenUpdateRatingToSameRating_thenShouldDoNothing() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Job updatedJob = job.updateRating(JobRating.of(3));
        assertSame(updatedJob, job);
        assertEquals(updatedJob.getStatus(), job.getStatus());
    }

    @Test
    void shouldUpdateRating() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant now = Instant.now();
        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .activities(Collections.emptyList())
                .attachments(Collections.emptyList())
                .build();

        Job updatedJob = job.updateRating(JobRating.of(5));
        assertEquals(JobRating.of(5), updatedJob.getRating());
    }

    @Test
    void shouldSaveFollowUpReminderSentAt() {
        UserId userId = UserId.generate();
        Job job = Job.builder()
                .id(JobId.generate())
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.CREATED)
                .rating(JobRating.of(3))
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .build();

        assertNull(job.getFollowUpReminderSentAt());
        Job updatedJob = job.saveFollowUpReminderSentAt();
        assertTrue(updatedJob.getFollowUpReminderSentAt().getEpochSecond() - Instant.now().getEpochSecond() < 1);
    }
}
