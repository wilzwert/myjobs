package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.batch.UsersJobsBatchResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendJobsRemindersUseCaseImplTest {

    @Mock
    private JobService jobService;
    @Mock
    private UserService userService;
    @Mock
    private JobReminderMessageProvider jobReminderMessageProvider;

    private SendJobsRemindersUseCaseImpl underTest;


    private List<User> testUsers;
    private List<Job> testJobs;

    @BeforeEach
    void setUp() {
        testUsers = List.of(
            User.builder()
                .id(new UserId(UUID.fromString("00000000-0000-0000-0000-000000000000")))
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .username("johndoe")
                .jobFollowUpReminderDays(7).build(),
            User.builder()
                .id(new UserId(UUID.fromString("11111111-1111-1111-1111-111111111111")))
                .email("user2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("password")
                .username("janesmith")
                .jobFollowUpReminderDays(7).build()
        );
        testJobs = List.of(
            Job.builder()
                    .id(JobId.generate())
                    .userId(testUsers.get(1).getId())
                    .title("User 2 scrum master")
                    .description("User 2 scrum master job description")
                    .profile("User 2 scrum master job profile")
                    .status(JobStatus.PENDING)
                    .url("http://www.example.com/user2/scrum")
                    .statusUpdatedAt(Instant.now().minusSeconds(86400*31)).build(),
            Job.builder()
                .id(JobId.generate())
                .userId(testUsers.getFirst().getId())
                .title("User1 developer")
                .description("User1 developer job description")
                .profile("User1 developer job profile")
                .status(JobStatus.PENDING)
                .url("http://www.example.com/user1/developer")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*10)).build(),
             Job.builder()
                .id(JobId.generate())
                .userId(testUsers.getFirst().getId())
                .title("User1 analyst")
                .description("User1 analyst job description")
                .profile("User1 analyst job profile")
                .status(JobStatus.RELAUNCHED)
                .url("http://www.example.com/user1/analyst")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*8)).build()
        );

        // Mock the JobService
        when(jobService.stream(ArgumentMatchers.<DomainSpecification.JobFollowUpToRemind>any())).thenReturn(testJobs.stream());

        underTest = new SendJobsRemindersUseCaseImpl(jobService, userService, jobReminderMessageProvider);
    }

    @Test
    void shouldReturnBatchResult() {
        // Mock the UserService
        when(userService.findMinimal(any()))
            .thenReturn(
                    Map.of(testUsers.get(1).getId(), testUsers.get(1))
            )
            .thenReturn(
                    Map.of(testUsers.getFirst().getId(), testUsers.getFirst())
            );

        List<UsersJobsBatchResult> result = underTest.sendJobsReminders(1);
        assertNotNull(result);
        System.out.println(result);
        verify(userService, times(2)).findMinimal(any());
        verify(jobReminderMessageProvider, times(2)).send(any(User.class), any());
    }
}


