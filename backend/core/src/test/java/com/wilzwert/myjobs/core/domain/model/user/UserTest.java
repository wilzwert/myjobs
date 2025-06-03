package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */

class UserTest {

    @Test
    void whenInvalid_thenUserBuildShouldThrowValidationException() {
        User.Builder builder = User.builder();
        ValidationException exception = assertThrows(ValidationException.class, builder::build);
        assertNotNull(exception);
        assertEquals(5, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("email").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("firstName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("lastName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("username").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    void whenEmailAndUsernameInvalid_thenUserBuildShouldThrowValidationException() {
        User.Builder builder = User.builder().username("T").email("invalid");
        ValidationException exception = assertThrows(ValidationException.class, builder::build);
        assertNotNull(exception);
        assertEquals(5, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.INVALID_EMAIL, exception.getErrors().getErrors().get("email").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("firstName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("lastName").getFirst().code());
        assertEquals(ErrorCode.FIELD_TOO_SHORT, exception.getErrors().getErrors().get("username").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    void whenFieldErrors_thenUserBuildShouldThrowValidationException() {
        User.Builder builder = User.builder().username("T").email("invalid").jobFollowUpReminderDays(45);
        ValidationException exception = assertThrows(ValidationException.class, builder::build);
        assertNotNull(exception);
        assertEquals(6, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.INVALID_EMAIL, exception.getErrors().getErrors().get("email").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("firstName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("lastName").getFirst().code());
        assertEquals(ErrorCode.FIELD_TOO_SHORT, exception.getErrors().getErrors().get("username").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("password").getFirst().code());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_BIG, exception.getErrors().getErrors().get("jobFollowUpReminderDays").getFirst().code());
    }

    @Test
    void shouldCreateUserWithDefaultValues() {
        UserId userId = new UserId(UUID.randomUUID());
        Instant before = Instant.now();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .jobs(Collections.emptyList())
                .build();
        Instant after = Instant.now();

        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals(User.DEFAULT_LANG, user.getLang());
        assertNotNull(user.getEmailValidationCode());
        assertEquals(EmailStatus.PENDING, user.getEmailStatus());
        assertEquals(User.DEFAULT_ROLE, user.getRole());
        assertEquals(User.DEFAULT_JOB_FOLLOW_UP_REMINDER_DAYS, user.getJobFollowUpReminderDays());
        assertNull(user.getResetPasswordToken());
        assertNull(user.getResetPasswordExpiresAt());
        Instant createdAt = user.getCreatedAt();
        Instant updatedAt = user.getUpdatedAt();
        assertTrue(createdAt.equals(before) || createdAt.equals(after) || createdAt.isAfter(before) && createdAt.isBefore(after));
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
        assertEquals(Collections.emptyList(), user.getJobs());
    }

    @Test
    void shouldCreateUser() {
        UserId userId = new UserId(UUID.randomUUID());
        Instant now = Instant.now();
        List<Job> jobs = List.of(Job.builder()
            .id(JobId.generate())
            .url("http://www.example.com")
            .title("Job title")
            .company("Job company")
            .description("Job description")
            .profile("Job profile")
            .salary("TBD")
            .userId(userId)

            .build()
        );
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .role("SOME_ROLE")
                .jobFollowUpReminderDays(7)
                .lang(Lang.FR)
                .createdAt(now)
                .updatedAt(now)
                .emailStatus(EmailStatus.VALIDATED)
                .emailValidationCode("code")
                .jobs(jobs)
                .build();



        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals(7, user.getJobFollowUpReminderDays());
        assertEquals(Lang.FR, user.getLang());
        assertEquals("SOME_ROLE", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals("code", user.getEmailValidationCode());
        assertEquals(1, user.getJobs().size());
        assertEquals("http://www.example.com", user.getJobs().getFirst().getUrl());
    }

    @Test
    void whenPasswordWeak_thenCreateUserShouldThrowValidationException() {
        var builder =  User.builder().email("test@example.com").password("password").username("username").firstName("firstName").lastName("lastName").jobFollowUpReminderDays(7);
        ValidationException exception = assertThrows(ValidationException.class, () ->
            User.create(builder,"weakPassword")
        );

        assertNotNull(exception);
        assertEquals(1, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.USER_WEAK_PASSWORD, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    void shouldCreateNewUser() {
        Instant before = Instant.now();
        User user = User.create(
                User.builder()
                    .email("test@example.com")
                    .password("password")
                    .username("username")
                    .firstName("firstName")
                    .lastName("lastName")
                    .jobFollowUpReminderDays(7)
                    .lang(Lang.FR)
                    .jobs(Collections.emptyList()),
        "Abcd1234!"
        );
        Instant after = Instant.now();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
        assertEquals(7, user.getJobFollowUpReminderDays());
        assertEquals(Lang.FR, user.getLang());
        assertNotNull(user.getEmailValidationCode());
        assertEquals(EmailStatus.PENDING, user.getEmailStatus());
        assertEquals("USER", user.getRole());
        assertNull(user.getResetPasswordToken());
        assertNull(user.getResetPasswordExpiresAt());
        Instant createdAt = user.getCreatedAt();
        Instant updatedAt = user.getUpdatedAt();
        assertTrue(createdAt.equals(before) || createdAt.equals(after) || createdAt.isAfter(before) && createdAt.isBefore(after));
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
        assertEquals(Collections.emptyList(), user.getJobs());
    }

    @Test
    void shouldUpdateUser() {
        UserId userId = UserId.generate();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .emailStatus(EmailStatus.VALIDATED)
                .emailValidationCode("code")
                .jobFollowUpReminderDays(6)
                .jobs(Collections.emptyList())
                .build();

        Instant before = Instant.now();
        User updatedUser = user.update("email@example.com", "changedUsername", "John", "Doe", 8);
        Instant after = Instant.now();

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("changedUsername", updatedUser.getUsername());
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals(8, updatedUser.getJobFollowUpReminderDays());
        assertEquals(User.DEFAULT_LANG, updatedUser.getLang());
        assertEquals("email@example.com", updatedUser.getEmail());

        Instant updatedAt = updatedUser.getUpdatedAt();
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
    }

    @Test
    void shouldUpdateUserLang() {
        UserId userId = UserId.generate();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .emailStatus(EmailStatus.VALIDATED)
                .emailValidationCode("code")
                .build();

        assertEquals(User.DEFAULT_LANG, user.getLang());

        User updatedUser = user.updateLang(Lang.FR);
        assertEquals(Lang.FR, updatedUser.getLang());
        assertNotSame(user, updatedUser);

    }
    @Test
    void shouldSaveJobFollowUpReminderSentAt() {
        UserId userId = UserId.generate();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .emailStatus(EmailStatus.VALIDATED)
                .emailValidationCode("code")
                .build();
        assertNull(user.getJobFollowUpReminderSentAt());
        User updatedUser = user.saveJobFollowUpReminderSentAt();
        assertTrue(updatedUser.getJobFollowUpReminderSentAt().getEpochSecond() - Instant.now().getEpochSecond() < 1);
    }

    @Test
    void shouldUpdateJob() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        Instant jobCreatedAt = Instant.parse("2025-03-09T13:45:30Z");
        Instant now = Instant.now();
        List<Activity> activities = List.of(Activity.builder()
                .id(ActivityId.generate())
                .createdAt(now)
                .updatedAt(now)
                .type(ActivityType.CREATION)
                .comment("Creation")
                .build());
        List<Attachment> attachments = List.of(Attachment.builder()
                .id(AttachmentId.generate())
                .createdAt(now)
                .updatedAt(now)
                .fileId("attachementFile1")
                .name("Attachment 1")
                .filename("attachment1.doc")
                .contentType("application/msword")
                .build());

        Job job = Job.builder()
                .id(jobId)
                .url("http://www.example.com")
                .title("Job title")
                .status(JobStatus.PENDING)
                .rating(JobRating.of(3))
                .company("Company")
                .description("Job description")
                .profile("Job profile")
                .comment("Job comment")
                .salary("TBD")
                .userId(userId)
                .createdAt(jobCreatedAt)
                .updatedAt(jobCreatedAt)
                .statusUpdatedAt(jobCreatedAt)
                .activities(activities)
                .attachments(attachments)
                .build();

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .emailStatus(EmailStatus.VALIDATED)
                .emailValidationCode("code")
                .jobs(List.of(job))
                .build();

        Instant before = Instant.now();
        User updatedUser = user.updateJob(job,"http://www.example.com/updated", "Job updated title", "Updated company", "Job updated description", "Job updated profile", "Job updated comment", "Job updated salary");
        Instant after = Instant.now();

        assertNotNull(updatedUser);

        Optional<Job> optionalUpdatedJob = updatedUser.getJobById(jobId);
        assertTrue(optionalUpdatedJob.isPresent());
        Job updatedJob = optionalUpdatedJob.get();
        assertEquals(jobId, updatedJob.getId());
        assertEquals(userId, updatedJob.getUserId());
        assertEquals(JobStatus.PENDING, updatedJob.getStatus());
        assertEquals("Job updated title", updatedJob.getTitle());
        assertEquals("http://www.example.com/updated", updatedJob.getUrl());
        assertEquals("Updated company", updatedJob.getCompany());
        assertEquals("Job updated description", updatedJob.getDescription());
        assertEquals("Job updated profile", updatedJob.getProfile());
        assertEquals("Job updated comment", updatedJob.getComment());
        assertEquals("Job updated salary", updatedJob.getSalary());
        Instant updatedAt = updatedJob.getUpdatedAt();
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
        assertEquals(jobCreatedAt, updatedJob.getCreatedAt());
        // status has not changed, check that the status update date also didn't change
        assertEquals(jobCreatedAt, updatedJob.getStatusUpdatedAt());
        assertEquals(1, updatedJob.getActivities().size());
        assertEquals(1, updatedJob.getAttachments().size());
    }


    @Nested
    class JobRetrievalTest {

        private User testUser;
        private Job testJob;


        @BeforeEach
        void setUp() {
            UserId userId = new UserId(UUID.randomUUID());
            JobId jobId = new JobId(UUID.randomUUID());
            Instant jobCreatedAt = Instant.parse("2025-03-09T13:45:30Z");
            Instant now = Instant.now();
            List<Activity> activities = List.of(Activity.builder()
                    .id(ActivityId.generate())
                    .createdAt(now)
                    .updatedAt(now)
                    .type(ActivityType.CREATION)
                    .comment("Creation")
                    .build());
            List<Attachment> attachments = List.of(Attachment.builder()
                    .id(AttachmentId.generate())
                    .createdAt(now)
                    .updatedAt(now)
                    .fileId("attachementFile1")
                    .name("Attachment 1")
                    .filename("attachment1.doc")
                    .contentType("application/msword")
                    .build());

            testJob = Job.builder()
                    .id(jobId)
                    .url("http://www.example.com")
                    .title("Job title")
                    .status(JobStatus.PENDING)
                    .rating(JobRating.of(3))
                    .company("Company")
                    .description("Job description")
                    .profile("Job profile")
                    .salary("TBD")
                    .userId(userId)
                    .createdAt(jobCreatedAt)
                    .updatedAt(jobCreatedAt)
                    .statusUpdatedAt(jobCreatedAt)
                    .activities(activities)
                    .attachments(attachments)
                    .build();

            testUser = User.builder()
                    .id(userId)
                    .email("test@example.com")
                    .password("password")
                    .username("username")
                    .firstName("firstName")
                    .lastName("lastName")
                    .emailStatus(EmailStatus.VALIDATED)
                    .emailValidationCode("code")
                    .jobs(List.of(testJob))
                    .build();
        }

        @Test
        void whenJobIdNotFound_thenShouldReturnEmptyOptional() {
            assertTrue(testUser.getJobById(JobId.generate()).isEmpty());
        }

        @Test
        void shouldRetrieveJobById() {
            var foundJob = testUser.getJobById(testJob.getId());
            assertTrue(foundJob.isPresent());
            assertEquals(testJob, foundJob.get());
        }

        @Test
        void whenUrlNotFound_thenShouldReturnEmptyOptional() {
            assertTrue(testUser.getJobByUrl("http://www.example.com/not-existing").isEmpty());
        }

        @Test
        void shouldRetrieveJobByUrl() {
            var foundJob = testUser.getJobByUrl(testJob.getUrl());
            assertTrue(foundJob.isPresent());
            assertEquals(testJob, foundJob.get());
        }
    }
}