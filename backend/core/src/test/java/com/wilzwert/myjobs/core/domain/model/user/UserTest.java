package com.wilzwert.myjobs.core.domain.model.user;


import com.wilzwert.myjobs.core.domain.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:16:45
 */

public class UserTest {

    @Test
    public void whenInvalid_thenUserBuildShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            User user = User.builder().build();
        });
        assertNotNull(exception);
        assertEquals(5, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("email").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("firstName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("lastName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("username").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    public void whenEmailAndUsernameInvalid_thenUserBuildShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            User user = User.builder().username("T").email("invalid").build();
        });
        assertNotNull(exception);
        assertEquals(5, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.INVALID_EMAIL, exception.getErrors().getErrors().get("email").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("firstName").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("lastName").getFirst().code());
        assertEquals(ErrorCode.FIELD_TOO_SHORT, exception.getErrors().getErrors().get("username").getFirst().code());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    public void shouldCreateUserWithDefaultValues() {
        UserId userId = new UserId(UUID.randomUUID());
        Instant before = Instant.now();
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("password")
                .username("username")
                .firstName("firstName")
                .lastName("lastName")
                .build();
        Instant after = Instant.now();

        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
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
    public void shouldCreateUser() {
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
        assertEquals("SOME_ROLE", user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
        assertEquals("code", user.getEmailValidationCode());
        assertEquals(1, user.getJobs().size());
        assertEquals("http://www.example.com", user.getJobs().get(0).getUrl());
    }

    @Test
    public void whenPasswordWeak_thenCreateUserShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () ->  {
            User user = User.create("test@example.com",  "password", "username", "firstName", "lastName", "weakPassword");
        });

        assertNotNull(exception);
        assertEquals(1, exception.getErrors().getErrors().entrySet().size());
        assertEquals(ErrorCode.USER_WEAK_PASSWORD, exception.getErrors().getErrors().get("password").getFirst().code());
    }

    @Test
    public void shouldCreateNewUser() {
        Instant before = Instant.now();
        User user = User.create("test@example.com",  "password", "username", "firstName", "lastName", "Abcd1234!");
        Instant after = Instant.now();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals("username", user.getUsername());
        assertEquals("firstName", user.getFirstName());
        assertEquals("lastName", user.getLastName());
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
    public void shouldUpdateUser() {
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

        Instant before = Instant.now();
        User updatedUser = user.update("email@example.com", "changedUsername", "John", "Doe");
        Instant after = Instant.now();

        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId());
        assertEquals("changedUsername", updatedUser.getUsername());
        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        assertEquals("email@example.com", updatedUser.getEmail());

        Instant updatedAt = updatedUser.getUpdatedAt();
        assertTrue(updatedAt.equals(before) || updatedAt.equals(after) || updatedAt.isAfter(before) && updatedAt.isBefore(after));
    }
}
