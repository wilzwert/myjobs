package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.*;
import com.wilzwert.myjobs.core.domain.shared.querying.criteria.DomainQueryingCriterion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobUseCaseImplTest {

    @Mock
    private JobService jobService;
    @Mock
    private UserService userService;
    @Mock
    private FileStorage fileStorage;
    @Mock
    private HtmlSanitizer htmlSanitizer;

    private JobUseCaseImpl underTest;

    private User testUser;
    private Job testJob;
    private Job testFollowUpLateJob;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UserId.generate())
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .username("johndoe")
                .jobFollowUpReminderDays(30).build();
        testJob = Job.builder()
                .id(JobId.generate())
                .userId(testUser.getId())
                .title("Developer")
                .description("job description")
                .profile("job profile")
                .status(JobStatus.PENDING)
                .url("http://www.example.com")
                .statusUpdatedAt(Instant.now().minusSeconds(3600)).build();
        testFollowUpLateJob = Job.builder()
                .id(JobId.generate())
                .userId(testUser.getId())
                .title("Developer")
                .description("job description")
                .profile("job profile")
                .status(JobStatus.PENDING)
                .url("http://www.example.com")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*31)).build();

        // Mock de l'UserService
        when(userService.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

        underTest = new JobUseCaseImpl(jobService, userService, fileStorage, htmlSanitizer);
    }

    @Test
    void whenUserNotFound_thenGetUserJobs_shouldThrowUserNotFoundException() {
        reset(userService);
        when(userService.findById(any(UserId.class))).thenReturn(Optional.empty());

        UserId userId = new UserId(UUID.randomUUID());

        assertThrows(UserNotFoundException.class, () -> underTest.getUserJobs(userId, 0, 10, JobStatus.PENDING, false, "date,desc"));
    }

    @Test
    void whenFilterLateTrue_thenShouldGetLateUserJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testFollowUpLateJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
        
        when(jobService.findByUserPaginated(eq(testUser), anyList(), eq(0), eq(10), eq("date,desc"))).thenReturn(mockJobPage);

        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, true, "date,desc");

        // check criteria passed to the jobService
        verify(jobService).findByUserPaginated(eq(testUser), argThat(criteria -> criteria.size() == 2 &&
               criteria.getFirst() instanceof DomainQueryingCriterion.In &&
               criteria.get(1) instanceof DomainQueryingCriterion.Lt), eq(0), eq(10), eq("date,desc"));

        // check results page is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
        assertTrue(result.getContent().getFirst().isFollowUpLate());
    }

    @Test
    void whenFilterLateFalse_thenShouldGetUserJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
        
        when(jobService.findAllByUserIdPaginated(testUser.getId(), 0, 10, JobStatus.PENDING, "date")).thenReturn(mockJobPage);

        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, false, "date");

        // Vérifier que le service est appelé sans critères de filtrage
        verify(jobService).findAllByUserIdPaginated(testUser.getId(), 0, 10, JobStatus.PENDING, "date");

        // check results page is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
    }

    @Test
    void testEnrichJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();

        when(jobService.findAllByUserIdPaginated(testUser.getId(), 0, 10, JobStatus.PENDING, "date")).thenReturn(mockJobPage);

        // call test method
        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, false, "date");

        // check result is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
        assertInstanceOf(EnrichedJob.class, result.getContent().getFirst());
    }
}