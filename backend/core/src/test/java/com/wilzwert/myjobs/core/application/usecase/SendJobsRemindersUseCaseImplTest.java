package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.batch.UsersJobsRemindersBulkResult;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.JobReminderMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
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
    private JobDataManager jobDataManager;
    @Mock
    private UserDataManager userDataManager;
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
                    .company("Company2")
                    .description("User 2 scrum master job description")
                    .profile("User 2 scrum master job profile")
                    .status(JobStatus.PENDING)
                    .url("http://www.example.com/user2/scrum")
                    .statusUpdatedAt(Instant.now().minusSeconds(86400*31)).build(),
            Job.builder()
                .id(JobId.generate())
                .userId(testUsers.getFirst().getId())
                .title("User1 developer")
                .company("Company1")
                .description("User1 developer job description")
                .profile("User1 developer job profile")
                .status(JobStatus.PENDING)
                .url("http://www.example.com/user1/developer")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*10)).build(),
             Job.builder()
                .id(JobId.generate())
                .userId(testUsers.getFirst().getId())
                .title("User1 analyst")
                 .company("Company3")
                .description("User1 analyst job description")
                .profile("User1 analyst job profile")
                .status(JobStatus.RELAUNCHED)
                .url("http://www.example.com/user1/analyst")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*8)).build()
        );

        // Mock the JobDataManager
        when(jobDataManager.stream(ArgumentMatchers.<DomainSpecification.JobFollowUpToRemind>any())).thenReturn(testJobs.stream());

        underTest = new SendJobsRemindersUseCaseImpl(jobDataManager, userDataManager, jobReminderMessageProvider);
    }

    @Test
    void shouldReturnBatchResult() {
        // Mock the UserDataManager
        when(userDataManager.findMinimal(any()))
            .thenReturn(
                    Map.of(testUsers.get(1).getId(), testUsers.get(1))
            )
            .thenReturn(
                    Map.of(testUsers.getFirst().getId(), testUsers.getFirst())
            );

        when(userDataManager.saveAll(any())).thenReturn(new BulkDataSaveResult(2, 2, 0, 0));

        List<UsersJobsRemindersBulkResult> result = underTest.sendJobsReminders(1);
        assertNotNull(result);
        verify(userDataManager, times(2)).findMinimal(any());
        verify(userDataManager, times(2)).saveAll(any());
        verify(jobReminderMessageProvider, times(2)).send(any(User.class), any());
    }
}


