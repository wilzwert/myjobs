package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.command.CreateJobCommand;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.EnrichedJob;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobService;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
        UserId testUserId = UserId.generate();
        testJob = Job.builder()
                .id(JobId.generate())
                .userId(testUserId)
                .title("Developer 1")
                .company("Company 1")
                .description("job description 1")
                .profile("job profile 1")
                .status(JobStatus.PENDING)
                .url("http://www.example.com/1")
                .statusUpdatedAt(Instant.now().minusSeconds(3600)).build();
        testFollowUpLateJob = Job.builder()
                .id(JobId.generate())
                .userId(testUserId)
                .title("Developer 2")
                .company("Company 2")
                .description("job description 2")
                .profile("job profile 2")
                .status(JobStatus.PENDING)
                .url("http://www.example.com/2")
                .statusUpdatedAt(Instant.now().minusSeconds(86400*31)).build();

        testUser = User.builder()
                .id(testUserId)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .username("johndoe")
                .jobFollowUpReminderDays(30)
                .jobs(List.of(
                    testJob,
                    testFollowUpLateJob
                )).build();

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
    void whenCreateAndJobExists_thenCreateJob_shouldThrowJobAlreadyExistsException() {
        reset(userService);
        when(userService.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

        assertThrows(JobAlreadyExistsException.class, () -> underTest.createJob(
                new CreateJobCommand.Builder()
                    .userId(UserId.generate())
                    .title("Job title")
                    .description("Job description")
                    .url("http://www.example.com/1")
                    .company("Company 3")
                    .build()
        ));
    }

    @Test
    void whenFilterLateTrue_thenShouldGetLateUserJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testFollowUpLateJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
        
        when(jobService.findPaginated(any(), eq(0), eq(10))).thenReturn(mockJobPage);

        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, true, "date,desc");

        // check specification passed to the jobService
        verify(jobService).findPaginated(
            argThat(specification -> {
                // TODO we MUST check userId, filter late and sort specs
                DomainSpecification.And spec = (DomainSpecification.And) specification;
                System.out.println(specification+ "->" +spec.getSpecifications().size());
                return  spec.getSpecifications().size() == 3 &&
                        spec.getSpecifications().getFirst() instanceof DomainSpecification.Eq &&
                        spec.getSpecifications().get(1) instanceof DomainSpecification.In<?> &&
                        spec.getSpecifications().get(2) instanceof DomainSpecification.Lt<?>;

            }), eq(0), eq(10));

        // check results page is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
        assertTrue(result.getContent().getFirst().isFollowUpLate());
    }

    @Test
    void whenFilterLateFalse_thenShouldGetUserJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
        
        when(jobService.findPaginated(any(DomainSpecification.class), eq(0), eq(10))).thenReturn(mockJobPage);

        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, false, "date");

        // TODO : check that the DomainSpecification is correctly built
        // as is, we only check that some spec has been built
        verify(jobService).findPaginated(any(DomainSpecification.class), eq(0), eq(10));

        // check results page is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
    }

    @Test
    void testEnrichJobs() {
        DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();

        when(jobService.findPaginated(any(DomainSpecification.class), eq(0), eq(10))).thenReturn(mockJobPage);

        // call test method
        DomainPage<EnrichedJob> result = underTest.getUserJobs(testUser.getId(), 0, 10, JobStatus.PENDING, false, "date");

        // check result is enriched
        assertNotNull(result);
        assertEquals(1, result.getTotalElementsCount());
        assertInstanceOf(EnrichedJob.class, result.getContent().getFirst());
    }

    @Nested
    class AttachmentTest {

        @Test
        void whenAttachmentNotFound_thenShouldThrowAttachmentNotFoundException() {
            reset(userService);
            when(jobService.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJob));
            assertThrows(AttachmentNotFoundException.class, () -> underTest.downloadAttachment(new DownloadAttachmentCommand("nonexistend", testJob.getUserId(), testJob.getId())));
        }
    }

    @Nested
    class ActivityTest {
        @Test
        void whenJobNotFound_thenShouldThrowJobNotFoundException() {
            reset(userService);
            when(jobService.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());
            assertThrows(JobNotFoundException.class, () -> underTest.addActivityToJob(new CreateActivityCommand(ActivityType.EMAIL, "comment", UserId.generate(), JobId.generate())));
        }
    }
}