package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.AttachmentFileInfo;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivitiesCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.CreateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.command.UpdateActivityCommand;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.exception.ActivityNotFoundException;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.CreateAttachmentsCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DeleteAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.command.DownloadAttachmentCommand;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentFileNotReadableException;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.*;
import com.wilzwert.myjobs.core.domain.model.job.command.*;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobFieldUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.JobUpdatedEvent;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.job.exception.JobNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.job.ports.driven.JobDataManager;
import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.HtmlSanitizer;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobUseCaseImplTest {

    @Mock
    private TransactionProvider transactionProvider;
    @Mock
    private IntegrationEventPublisher integrationEventPublisher;
    @Mock
    private JobDataManager jobDataManager;
    @Mock
    private UserDataManager userDataManager;
    @Mock
    private FileStorage fileStorage;
    @Mock
    private HtmlSanitizer htmlSanitizer;

    private JobUseCaseImpl underTest;

    private User testUser;
    private Job testJob;
    private Job testFollowUpLateJob;
    private AttachmentId attachmentId;

    @BeforeEach
    void setUp() {
        UserId testUserId = UserId.generate();
        attachmentId = AttachmentId.generate();
        testJob = Job.builder()
                .id(JobId.generate())
                .userId(testUserId)
                .title("Developer 1")
                .company("Company 1")
                .description("job description 1")
                .profile("job profile 1")
                .status(JobStatus.PENDING)
                .url("http://www.example.com/1")
                .rating(JobRating.of(5))
                .attachments(List.of(
                        Attachment.builder()
                                .id(attachmentId)
                                .name("Attachment name")
                                .fileId("fileId")
                                .filename("attachment.pdf")
                                .contentType("application/pdf")
                                .build()
                ))
                .activities(Collections.emptyList())
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
                .attachments(Collections.emptyList())
                .activities(Collections.emptyList())
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

        // Mocks UserDataManager
        when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
        underTest = new JobUseCaseImpl(transactionProvider, integrationEventPublisher, jobDataManager, userDataManager, fileStorage, htmlSanitizer);
    }

    @Nested
    class GetJobTest {

        @Test
        void whenUserNotFound_thenGetUserJob_shouldThrowUserNotFoundException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            when(jobDataManager.findByIdAndUserId(any(JobId.class), any(UserId.class))).thenReturn(Optional.of(testFollowUpLateJob));

            assertThrows(UserNotFoundException.class, () -> underTest.getUserJob(UserId.generate(), JobId.generate()));
        }

        @Test
        void whenJobNotFound_thenGetUserJob_shouldThrowJobNotFoundException() {
            // no need to actively mock the userDataManager as we expect the JobNotFoundException to be thrown before it is called
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(JobId.class), any(UserId.class))).thenReturn(Optional.empty());
            assertThrows(JobNotFoundException.class, () -> underTest.getUserJob(UserId.generate(), JobId.generate()));
        }

        @Test
        void shouldGetJob() {
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testJob.getUserId())).thenReturn(Optional.of(testJob));

            // call test method
            EnrichedJob enrichedJob = underTest.getUserJob(testUser.getId(), testJob.getId());


            verify(jobDataManager).findByIdAndUserId(testJob.getId(), testJob.getUserId());
            verify(userDataManager).findById(any(UserId.class));

            // check result is enriched
            assertNotNull(enrichedJob);
            assertEquals(testJob, enrichedJob.job());
        }
    }

    @Nested
    class GetJobsTest {

        @Test
        void whenUserNotFound_thenGetUserJobs_shouldThrowUserNotFoundException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            UserId userId = new UserId(UUID.randomUUID());
            GetUserJobsCommand command = new GetUserJobsCommand.Builder()
                    .userId(userId)
                    .page(0)
                    .itemsPerPage(10)
                    .status(JobStatus.PENDING)
                    .sort("date,desc")
                    .build();

            assertThrows(UserNotFoundException.class, () -> underTest.getUserJobs(command));
        }


        @Test
        void whenFilterLate_thenShouldGetLateUserJobs() {
            DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testFollowUpLateJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();

            when(jobDataManager.findPaginated(any(), eq(0), eq(10))).thenReturn(mockJobPage);

            GetUserJobsCommand command = new GetUserJobsCommand.Builder()
                    .userId(testUser.getId())
                    .page(0)
                    .itemsPerPage(10)
                    .status(JobStatus.PENDING)
                    .statusMeta(JobStatusMeta.LATE)
                    .sort("date,desc")
                    .build();
            DomainPage<EnrichedJob> result = underTest.getUserJobs(command);

            // check specification passed to the jobDataManager
            verify(jobDataManager).findPaginated(
                    argThat(specification -> {
                        // TODO we MUST check userId, filter late and sort specs
                        DomainSpecification.And spec = (DomainSpecification.And) specification;

                        // specs include spec for JobStatus.PENDING + specs for the late filter
                        return spec.getSpecifications().size() == 4 &&
                                // user id
                                spec.getSpecifications().getFirst() instanceof DomainSpecification.Eq &&
                                // active statuses for late filter
                                spec.getSpecifications().get(1) instanceof DomainSpecification.In<?> &&
                                // status update
                                spec.getSpecifications().get(2) instanceof DomainSpecification.Lt<?> &&
                                // JobStatus.PENDING
                                spec.getSpecifications().get(3) instanceof DomainSpecification.Eq<?>;

                    }), eq(0), eq(10));

            // check results page is enriched
            assertNotNull(result);
            assertEquals(1, result.getTotalElementsCount());
            assertTrue(result.getContent().getFirst().isFollowUpLate());
        }

        @Test
        void whenQuery_thenShouldGetUserJobsWithQuery() {
            DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testFollowUpLateJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();

            when(jobDataManager.findPaginated(any(), eq(0), eq(10))).thenReturn(mockJobPage);

            GetUserJobsCommand command = new GetUserJobsCommand.Builder()
                    .userId(testUser.getId())
                    .page(0)
                    .itemsPerPage(10)
                    .status(JobStatus.PENDING)
                    .query("search something")
                    .sort("date,desc")
                    .build();
            DomainPage<EnrichedJob> result = underTest.getUserJobs(command);

            // check specification passed to the jobDataManager
            verify(jobDataManager).findPaginated(
                    argThat(specification -> {
                        // TODO we MUST check userId, filter late and sort specs
                        DomainSpecification.And spec = (DomainSpecification.And) specification;

                        // specs include spec for JobStatus.PENDING + specs for the late filter
                        return spec.getSpecifications().size() == 3 &&
                                // user id
                                spec.getSpecifications().getFirst() instanceof DomainSpecification.Eq &&
                                // JobStatus.PENDING
                                spec.getSpecifications().get(1) instanceof DomainSpecification.Eq<?> &&
                                // query
                                spec.getSpecifications().get(2) instanceof DomainSpecification.MatchQuerySpecification matchQuerySpecification &&
                                matchQuerySpecification.getQuery().equals("search something");

                    }), eq(0), eq(10));

            // check results page is enriched
            assertNotNull(result);
            assertEquals(1, result.getTotalElementsCount());
            assertTrue(result.getContent().getFirst().isFollowUpLate());
        }

        @Test
        void whenFilterLateFalse_thenShouldGetUserJobs() {
            DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
            GetUserJobsCommand command = new GetUserJobsCommand.Builder()
                    .userId(testUser.getId())
                    .page(0)
                    .itemsPerPage(10)
                    .status(JobStatus.PENDING)
                    .sort("date")
                    .build();

            when(jobDataManager.findPaginated(any(DomainSpecification.class), eq(0), eq(10))).thenReturn(mockJobPage);

            DomainPage<EnrichedJob> result = underTest.getUserJobs(command);

            // TODO : check that the DomainSpecification is correctly built
            // as is, we only check that some spec has been built
            verify(jobDataManager).findPaginated(any(DomainSpecification.class), eq(0), eq(10));

            // check results page is enriched
            assertNotNull(result);
            assertEquals(1, result.getTotalElementsCount());
        }

        @Test
        void testEnrichJobs() {
            DomainPage<Job> mockJobPage = DomainPage.builder(List.of(testJob)).pageSize(1).currentPage(0).totalElementsCount(1).build();
            GetUserJobsCommand command = new GetUserJobsCommand.Builder()
                    .userId(testUser.getId())
                    .page(0)
                    .itemsPerPage(10)
                    .status(JobStatus.PENDING)
                    .sort("date")
                    .build();

            when(jobDataManager.findPaginated(any(DomainSpecification.class), eq(0), eq(10))).thenReturn(mockJobPage);

            // call test method
            DomainPage<EnrichedJob> result = underTest.getUserJobs(command);

            // check result is enriched
            assertNotNull(result);
            assertEquals(1, result.getTotalElementsCount());
            assertInstanceOf(EnrichedJob.class, result.getContent().getFirst());
        }
    }

    @Nested
    class CreateJobTest {
        @Test
        void whenUserNotFound_thenCreateJob_shouldThrowUserNotFoundException() {
            UserId userId = new UserId(UUID.randomUUID());

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            var command = new CreateJobCommand.Builder()
                            .userId(userId)
                            .title("Create job")
                            .company("company 3")
                            .build();

            assertThrows(UserNotFoundException.class, () -> underTest.createJob(command));
        }

        @Test
        void whenJobAlreadyExists_thenCreateJob_shouldThrowJobAlreadyExistsException() {
            UserId userId = new UserId(UUID.randomUUID());

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());

            var command = new CreateJobCommand.Builder()
                    .userId(userId)
                    .title("New job")
                    .company("Company 3")
                    .url(testJob.getUrl())
                    .description("New job description")
                    .build();

            assertThrows(JobAlreadyExistsException.class, () -> underTest.createJob(command));
            verify(userDataManager).findById(any(UserId.class));
            // sanitizer should have been called for title, company, description
            verify(htmlSanitizer, times(3)).sanitize(any(String.class));
        }

        @Test
        void shouldCreateJob() {
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
            ArgumentCaptor<JobCreatedEvent> jobCreatedEventArgumentCaptor = ArgumentCaptor.forClass(JobCreatedEvent.class);
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            // mocks htmlSanitizer
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            // using ArgumentCaptor for both user and job allows us to check that upon saving, they are as expected
            // we cannot just check that the current testUSer had been updated, as the domain always makes copies of entities
            // to ensure their immutability
            when(userDataManager.saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(jobCreatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            var command = new CreateJobCommand.Builder()
                    .userId(testUser.getId())
                    .title("New job")
                    .company("Company 3")
                    .url("https://www.example.com/new-job")
                    .description("New job description")
                    .profile("New job profile")
                    .comment("New job comment")
                    .build();

            assertDoesNotThrow(() -> underTest.createJob(command));
            verify(userDataManager).findById(any(UserId.class));
            verify(userDataManager).saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture());
            // sanitizer should have been called rot title, company, description
            verify(htmlSanitizer, times(5)).sanitize(any(String.class));
            verify(integrationEventPublisher).publish(jobCreatedEventArgumentCaptor.capture());

            User user = userArgumentCaptor.getValue();
            assertNotNull(user);
            assertEquals(3, user.getJobs().size());

            Job job = jobArgumentCaptor.getValue();
            assertNotNull(job);
            assertEquals("New job", job.getTitle());
            assertEquals("New job description", job.getDescription());
            assertEquals("New job profile", job.getProfile());
            assertEquals("New job comment", job.getComment());
            assertEquals("https://www.example.com/new-job", job.getUrl());
            assertEquals("Company 3", job.getCompany());

            JobCreatedEvent jobCreatedEvent = jobCreatedEventArgumentCaptor.getValue();
            assertNotNull(jobCreatedEvent);
            assertEquals(job.getId(), jobCreatedEvent.getJobId());
        }
    }

    @Nested
    class UpdateJobTest {
        @Test
        void whenUserNotFound_thenUpdateJob_shouldThrowUserNotFoundException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            var command = new UpdateJobFullCommand.Builder()
                    .jobId(JobId.generate())
                    .userId(UserId.generate())
                    .title("UpdatedJob")
                    .build();

            assertThrows(UserNotFoundException.class, () -> underTest.updateJob(command));
        }

        @Test
        void whenJobNotFound_thenUpdateJob_shouldThrowJobAlreadyExistsException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            var command = new UpdateJobFullCommand.Builder()
                    .jobId(JobId.generate())
                    .userId(UserId.generate())
                    .title("UpdatedJob")
                    .build();

            assertThrows(JobNotFoundException.class, () -> underTest.updateJob(command));
        }

        @Test
        void whenUpdatingAlreadyExistingUrl_thenUpdateJob_shouldThrowJobAlreadyExistsException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testUser.getId())).thenReturn(Optional.of(testJob));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());

            var command = new UpdateJobFullCommand.Builder()
                    .jobId(testJob.getId())
                    .userId(UserId.generate())
                    .title("UpdatedJob")
                    .url("http://www.example.com/2")
                    .build();

            assertThrows(JobAlreadyExistsException.class, () -> underTest.updateJob(command));
        }

        @Test
        void shouldUpdateJob() {
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
            ArgumentCaptor<JobUpdatedEvent> jobUpdatedEventArgumentCaptor = ArgumentCaptor.forClass(JobUpdatedEvent.class);

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testUser.getId())).thenReturn(Optional.of(testJob));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            when(userDataManager.saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(jobUpdatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            var command = new UpdateJobFullCommand.Builder()
                    .jobId(testJob.getId())
                    .userId(UserId.generate())
                    .url("http://www.example.com/new-job")
                    .title("Updated job title")
                    .company("Updated company")
                    .description("Updated job description")
                    .profile("Updated job profile")
                    .comment("Updated job comment")
                    .salary("Updated job salary")
                    .build();

            var updatedJob = assertDoesNotThrow(() -> underTest.updateJob(command));
            verify(jobDataManager).findByIdAndUserId(testJob.getId(), testUser.getId());
            verify(userDataManager).saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture());
            verify(integrationEventPublisher).publish(jobUpdatedEventArgumentCaptor.capture());

            User user = userArgumentCaptor.getValue();
            assertNotNull(user);
            assertEquals(2, user.getJobs().size());

            Job job = jobArgumentCaptor.getValue();
            assertNotNull(job);
            assertEquals(updatedJob, job);
            assertEquals("Updated job title", updatedJob.getTitle());
            assertEquals("Updated company", updatedJob.getCompany());
            assertEquals("http://www.example.com/new-job", updatedJob.getUrl());
            assertEquals("Updated job description", updatedJob.getDescription());
            assertEquals("Updated job profile", updatedJob.getProfile());
            assertEquals("Updated job comment", updatedJob.getComment());
            assertEquals("Updated job salary", updatedJob.getSalary());
            assertTrue(updatedJob.getUpdatedAt().getEpochSecond() - Instant.now().getEpochSecond() < 1);

            JobUpdatedEvent jobUpdatedEvent = jobUpdatedEventArgumentCaptor.getValue();
            assertNotNull(jobUpdatedEvent);
            assertEquals(job.getId(), jobUpdatedEvent.getJobId());
        }
    }

    @Nested
    class UpdateJobFieldTest {
        @Test
        void whenUserNotFound_thenUpdateJobField_shouldThrowUserNotFoundException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            var command = new UpdateJobFieldCommand.Builder()
                    .jobId(JobId.generate())
                    .userId(UserId.generate())
                    .field(UpdateJobFieldCommand.Field.TITLE)
                    .value("UpdatedJob")
                    .build();

            assertThrows(UserNotFoundException.class, () -> underTest.updateJobField(command));
        }

        @Test
        void whenJobNotFound_thenUpdateJobField_shouldThrowJobAlreadyExistsException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            var command = new UpdateJobFieldCommand.Builder()
                    .jobId(JobId.generate())
                    .userId(UserId.generate())
                    .field(UpdateJobFieldCommand.Field.TITLE)
                    .value("UpdatedJob")
                    .build();

            assertThrows(JobNotFoundException.class, () -> underTest.updateJobField(command));
        }

        @Test
        void whenUpdatingAlreadyExistingUrl_thenUpdateJobField_shouldThrowJobAlreadyExistsException() {
            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testUser.getId())).thenReturn(Optional.of(testJob));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            var command = new UpdateJobFieldCommand.Builder()
                    .jobId(testJob.getId())
                    .userId(UserId.generate())
                    .field(UpdateJobFieldCommand.Field.URL)
                    .value("http://www.example.com/2")
                    .build();

            assertThrows(JobAlreadyExistsException.class, () -> underTest.updateJobField(command));
        }

        @Test
        void shouldUpdateJobField() {
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
            ArgumentCaptor<JobFieldUpdatedEvent> jobFieldUpdatedEventArgumentCaptor = ArgumentCaptor.forClass(JobFieldUpdatedEvent.class);

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testUser.getId())).thenReturn(Optional.of(testJob));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            when(userDataManager.saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(jobFieldUpdatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            var command = new UpdateJobFieldCommand.Builder()
                    .jobId(testJob.getId())
                    .userId(UserId.generate())
                    .field(UpdateJobFieldCommand.Field.URL)
                    .value("http://www.example.com/new-job")
                    .build();

            var updatedJob = assertDoesNotThrow(() -> underTest.updateJobField(command));
            verify(jobDataManager).findByIdAndUserId(testJob.getId(), testUser.getId());
            verify(userDataManager).saveUserAndJob(userArgumentCaptor.capture(), jobArgumentCaptor.capture());
            verify(integrationEventPublisher).publish(jobFieldUpdatedEventArgumentCaptor.capture());

            User user = userArgumentCaptor.getValue();
            assertNotNull(user);
            assertEquals(2, user.getJobs().size());

            Job job = jobArgumentCaptor.getValue();
            assertNotNull(job);
            assertEquals(updatedJob, job);
            assertEquals(testJob.getTitle(), updatedJob.getTitle());
            assertEquals(testJob.getCompany(), updatedJob.getCompany());
            assertEquals("http://www.example.com/new-job", updatedJob.getUrl());
            assertEquals(testJob.getDescription(), updatedJob.getDescription());
            assertEquals(testJob.getProfile(), updatedJob.getProfile());
            assertEquals(testJob.getComment(), updatedJob.getComment());
            assertEquals(testJob.getSalary(), updatedJob.getSalary());
            assertTrue(updatedJob.getUpdatedAt().getEpochSecond() - Instant.now().getEpochSecond() < 1);

            JobFieldUpdatedEvent jobFieldUpdatedEvent = jobFieldUpdatedEventArgumentCaptor.getValue();
            assertNotNull(jobFieldUpdatedEvent);
            assertEquals(job.getId(), jobFieldUpdatedEvent.getJobId());
        }
    }

    @Nested
    class DeleteJobTest {
        @Test
        void whenUserNotFound_thenDeleteJob_shouldThrowUserNotFoundException() {
            JobId jobId = new JobId(UUID.randomUUID());
            UserId userId = new UserId(UUID.randomUUID());

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.empty());

            var command = new DeleteJobCommand(jobId, userId);

            assertThrows(UserNotFoundException.class, () -> underTest.deleteJob(command));
        }

        @Test
        void whenJobNotFound_thenDeleteJob_shouldThrowJobAlreadyExistsException() {
            JobId jobId = new JobId(UUID.randomUUID());
            UserId userId = new UserId(UUID.randomUUID());

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            var command = new DeleteJobCommand(jobId, userId);

            assertThrows(JobNotFoundException.class, () -> underTest.deleteJob(command));
        }

        @Test
        void shouldDeleteJob() {
            ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
            ArgumentCaptor<JobDeletedEvent> jobDeletedEventArgumentCaptor = ArgumentCaptor.forClass(JobDeletedEvent.class);

            reset(userDataManager);
            when(userDataManager.findById(any(UserId.class))).thenReturn(Optional.of(testUser));
            when(jobDataManager.findByIdAndUserId(testJob.getId(), testUser.getId())).thenReturn(Optional.of(testJob));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            when(userDataManager.deleteJobAndSaveUser(userArgumentCaptor.capture(), jobArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            doNothing().when(fileStorage).delete("fileId");
            when(integrationEventPublisher.publish(jobDeletedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            var command = new DeleteJobCommand(testJob.getId(), testUser.getId());

            assertDoesNotThrow(() -> underTest.deleteJob(command));
            verify(jobDataManager).findByIdAndUserId(testJob.getId(), testUser.getId());
            verify(transactionProvider).executeInTransaction(any(Supplier.class));
            verify(fileStorage).delete("fileId");
            verify(userDataManager).deleteJobAndSaveUser(userArgumentCaptor.capture(), jobArgumentCaptor.capture());
            verify(integrationEventPublisher).publish(jobDeletedEventArgumentCaptor.capture());

            User user = userArgumentCaptor.getValue();
            assertNotNull(user);
            assertEquals(1, user.getJobs().size());

            Job job = jobArgumentCaptor.getValue();
            assertNotNull(job);
            assertEquals(testJob.getId(), job.getId());

            JobDeletedEvent jobDeletedEvent = jobDeletedEventArgumentCaptor.getValue();
            assertNotNull(jobDeletedEvent);
            assertEquals(job.getId(), jobDeletedEvent.getJobId());
        }
    }

    @Nested
    class AttachmentTest {

        private Attachment attachment;

        private Job testJobWithAttachment;

        @BeforeEach
        void setUp() {
            attachment = Attachment.builder()
                    .contentType("application/msword")
                    .id(AttachmentId.generate())
                    .name("My file")
                    .fileId("notReadable")
                    .filename("notReadable.doc")
                    .build();
            testJobWithAttachment = Job.builder()
                    .id(JobId.generate())
                    .userId(UserId.generate())
                    .title("With attachment")
                    .company("Company")
                    .description("Job description")
                    .profile("Job profile")
                    .status(JobStatus.PENDING)
                    .url("http://www.example.com/1")
                    .attachments(List.of(attachment))
                    .activities(Collections.emptyList())
                    .statusUpdatedAt(Instant.now().minusSeconds(3600)).build();
        }

        @Test
        void shouldCreateAttachments() {
            ArgumentCaptor<Attachment> attachmentArg = ArgumentCaptor.forClass(Attachment.class);
            ArgumentCaptor<Activity> activityArg = ArgumentCaptor.forClass(Activity.class);
            ArgumentCaptor<Job> jobArg = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<AttachmentCreatedEvent> attachmentCreatedEventArgumentCaptor = ArgumentCaptor.forClass(AttachmentCreatedEvent.class);
            ArgumentCaptor<ActivityAutomaticallyCreatedEvent> activityAutomaticallyCreatedEventArgumentCaptor = ArgumentCaptor.forClass(ActivityAutomaticallyCreatedEvent.class);
            File file = mock(File.class);
            Instant before = Instant.now();

            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJob));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            when(fileStorage.store(eq(file), any(String.class), eq("test.pdf"))).thenReturn(
                    new DownloadableFile("newFileId", "stored/newFileId.pdf", "application/pdf", "test.pdf")
            );
            when(jobDataManager.saveJobAndAttachment(jobArg.capture(), attachmentArg.capture(), activityArg.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(attachmentCreatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(activityAutomaticallyCreatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));

            List<Attachment> result = underTest.addAttachmentsToJob(
                    new CreateAttachmentsCommand(
                        List.of(new CreateAttachmentCommand("my file", file, "test.pdf"))
                        , testJob.getUserId(), testJob.getId()
                    )
            );

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(jobDataManager).findByIdAndUserId(any(), any());
            verify(transactionProvider).executeInTransaction(any(Supplier.class));
            verify(fileStorage).store(eq(file), any(String.class), eq("test.pdf"));
            verify(jobDataManager).saveJobAndAttachment(jobArg.capture(), attachmentArg.capture(), activityArg.capture());
            verify(integrationEventPublisher).publish(attachmentCreatedEventArgumentCaptor.capture());
            verify(integrationEventPublisher).publish(activityAutomaticallyCreatedEventArgumentCaptor.capture());

            // check created/updated entities
            Attachment attachment = result.getFirst();
            assertEquals("my file", attachment.getName());
            assertEquals("newFileId", attachment.getFileId());
            assertEquals("test.pdf", attachment.getFilename());

            assertTrue(attachment.getCreatedAt().isAfter(before) || attachment.getUpdatedAt().equals(before));

            Job job = jobArg.getValue();
            assertNotNull(job);
            assertEquals(2, job.getAttachments().size());

            Activity activity = activityArg.getValue();
            assertNotNull(activity);
            assertEquals(activity, job.getActivities().getFirst());
            assertEquals(ActivityType.ATTACHMENT_CREATION, activity.getType());

            AttachmentCreatedEvent attachmentCreatedEvent = attachmentCreatedEventArgumentCaptor.getValue();
            assertNotNull(attachmentCreatedEvent);
            assertEquals(job.getId(), attachmentCreatedEvent.getJobId());
            assertEquals(attachment.getId(), attachmentCreatedEvent.getAttachmentId());

            ActivityAutomaticallyCreatedEvent activityAutomaticallyCreatedEvent = activityAutomaticallyCreatedEventArgumentCaptor.getValue();
            assertNotNull(activityAutomaticallyCreatedEvent);
            assertEquals(job.getId(), activityAutomaticallyCreatedEvent.getJobId());
            assertEquals(activity.getType(), activityAutomaticallyCreatedEvent.getActivityType());
        }

        @Test
        void whenAttachmentNotFound_thenShouldThrowAttachmentNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJob));

            DownloadAttachmentCommand command = new DownloadAttachmentCommand("nonexistent", testJob.getUserId(), testJob.getId());

            assertThrows(AttachmentNotFoundException.class, () -> underTest.downloadAttachment(command));
        }

        @Test
        void whenAttachmentNotReadable_thenShouldThrowAttachmentFileNotReadableException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJobWithAttachment));
            when(fileStorage.retrieve("notReadable", "notReadable.doc")).thenThrow(AttachmentFileNotReadableException.class);
            var command = new DownloadAttachmentCommand(attachment.getId().value().toString(), testJobWithAttachment.getUserId(), testJobWithAttachment.getId());

            assertThrows(AttachmentFileNotReadableException.class, () -> underTest.downloadAttachment(command));
            verify(jobDataManager).findByIdAndUserId(any(), any());
            verify(fileStorage).retrieve("notReadable", "notReadable.doc");
        }

        @Test
        void whenAttachmentExists_thenShouldDelete() {
            ArgumentCaptor<Activity> activityArg = ArgumentCaptor.forClass(Activity.class);
            ArgumentCaptor<Job> jobArg = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<AttachmentDeletedEvent> attachmentDeletedEventArgumentCaptor = ArgumentCaptor.forClass(AttachmentDeletedEvent.class);
            ArgumentCaptor<ActivityAutomaticallyCreatedEvent> activityAutomaticallyCreatedEventArgumentCaptor = ArgumentCaptor.forClass(ActivityAutomaticallyCreatedEvent.class);

            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJobWithAttachment));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            doNothing().when(fileStorage).delete(attachment.getFileId());
            when(integrationEventPublisher.publish(attachmentDeletedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(activityAutomaticallyCreatedEventArgumentCaptor.capture())).thenAnswer(i -> i.getArgument(0));
            when(jobDataManager.deleteAttachmentAndSaveJob(jobArg.capture(), eq(attachment), activityArg.capture())).thenAnswer(i -> i.getArgument(0));

            underTest.deleteAttachment(new DeleteAttachmentCommand(attachment.getId(), testJobWithAttachment.getUserId(), testJobWithAttachment.getId()));

            verify(jobDataManager).deleteAttachmentAndSaveJob(jobArg.capture(), eq(attachment), activityArg.capture());
            verify(fileStorage).delete(attachment.getFileId());
            verify(transactionProvider).executeInTransaction(any(Supplier.class));
            verify(integrationEventPublisher).publish(attachmentDeletedEventArgumentCaptor.capture());
            verify(integrationEventPublisher).publish(activityAutomaticallyCreatedEventArgumentCaptor.capture());

            // check that an activity has been created, and has been passed to the jobservice
            Job job = jobArg.getValue();
            Activity activity = activityArg.getValue();
            assertEquals(1, job.getActivities().size());
            assertEquals(activity, job.getActivities().getFirst());
            assertEquals(ActivityType.ATTACHMENT_DELETION, job.getActivities().getFirst().getType());

            AttachmentDeletedEvent attachmentDeletedEvent = attachmentDeletedEventArgumentCaptor.getValue();
            assertNotNull(attachmentDeletedEvent);
            assertEquals(job.getId(), attachmentDeletedEvent.getJobId());
            assertEquals(attachment.getId(), attachmentDeletedEvent.getAttachmentId());

            ActivityAutomaticallyCreatedEvent activityAutomaticallyCreatedEvent = activityAutomaticallyCreatedEventArgumentCaptor.getValue();
            assertNotNull(activityAutomaticallyCreatedEvent);
            assertEquals(job.getId(), activityAutomaticallyCreatedEvent.getJobId());
            assertEquals(activity.getType(), activityAutomaticallyCreatedEvent.getActivityType());
        }
    }

    @Nested
    class ActivityTest {

        private Activity activity;

        private Job testJobWithActivity;

        @BeforeEach
        void setUp() {
            activity = Activity.builder()
                    .id(ActivityId.generate())
                    .type(ActivityType.APPLICATION)
                    .createdAt(Instant.now().minusSeconds(86_400 * 7))
                    .updatedAt(Instant.now().minusSeconds(86_400 * 4))
                    .comment("activity comment")
                    .build();
            testJobWithActivity = Job.builder()
                    .id(JobId.generate())
                    .userId(UserId.generate())
                    .title("With attachment")
                    .company("Company")
                    .description("Job description")
                    .profile("Job profile")
                    .status(JobStatus.PENDING)
                    .url("http://www.example.com/1")
                    .activities(List.of(activity))
                    .attachments(Collections.emptyList())
                    .statusUpdatedAt(Instant.now().minusSeconds(3600)).build();
        }


        @Test
        void whenJobNotFound_thenShouldThrowJobNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());
            var command = new CreateActivitiesCommand(
                List.of(new CreateActivityCommand(ActivityType.EMAIL, "comment")),
                UserId.generate(),
                JobId.generate()
            );
            assertThrows(JobNotFoundException.class, () -> underTest.addActivitiesToJob(command));
        }

        @Test
        void shouldCreateActivity() {
            ArgumentCaptor<Activity> activityArg = ArgumentCaptor.forClass(Activity.class);
            ArgumentCaptor<Job> jobArg = ArgumentCaptor.forClass(Job.class);
            ArgumentCaptor<ActivityCreatedEvent> activityCreatedEventArg = ArgumentCaptor.forClass(ActivityCreatedEvent.class);
            Instant before = Instant.now();

            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(testJobWithActivity.getId(), testJobWithActivity.getUserId())).thenReturn(Optional.of(this.testJobWithActivity));
            when(htmlSanitizer.sanitize(any(String.class))).thenAnswer(i -> i.getArgument(0));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
            when(jobDataManager.saveJobAndActivity(jobArg.capture(), activityArg.capture())).thenAnswer(i -> i.getArgument(0));
            when(integrationEventPublisher.publish(activityCreatedEventArg.capture())).thenAnswer(i -> i.getArgument(0));

            Activity result = underTest.addActivitiesToJob(
                    new CreateActivitiesCommand(
                        List.of(new CreateActivityCommand(ActivityType.EMAIL, "new activity comment")),
                        testJobWithActivity.getUserId(),
                        testJobWithActivity.getId()
                    )
            ).getFirst();

            verify(jobDataManager).findByIdAndUserId(testJobWithActivity.getId(), testJobWithActivity.getUserId());
            verify(jobDataManager).saveJobAndActivity(jobArg.capture(), activityArg.capture());

            assertNotEquals(activity.getId(), result.getId());
            assertEquals(ActivityType.EMAIL, result.getType());
            assertEquals("new activity comment", result.getComment());
            assertTrue(result.getUpdatedAt().isAfter(before) || result.getUpdatedAt().equals(before));

            Job updatedJob = jobArg.getValue();
            assertNotNull(updatedJob);
            assertEquals(2, updatedJob.getActivities().size());
            // check activities order (most recent first)
            assertEquals(result, updatedJob.getActivities().getFirst());
            assertEquals(activity, updatedJob.getActivities().get(1));

            ActivityCreatedEvent activityCreatedEvent = activityCreatedEventArg.getValue();
            assertNotNull(activityCreatedEvent);
            assertEquals(result.getId(), activityCreatedEvent.getActivityId());
            assertEquals(result.getType(), activityCreatedEvent.getActivityType());
            assertEquals(updatedJob.getId(), activityCreatedEvent.getJobId());

        }

        @Test
        void whenJobNotFound_thenUpdateActivityShouldThrowJobNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());
            var command = new UpdateActivityCommand(activity.getId(), ActivityType.EMAIL, "comment", UserId.generate(), JobId.generate());
            assertThrows(JobNotFoundException.class, () -> underTest.updateActivity(command));
        }

        @Test
        void shouldUpdateActivity() {
            ArgumentCaptor<Activity> activityArg = ArgumentCaptor.forClass(Activity.class);
            ArgumentCaptor<Job> jobArg = ArgumentCaptor.forClass(Job.class);
            Instant before = Instant.now();

            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(testJobWithActivity.getId(), testJobWithActivity.getUserId())).thenReturn(Optional.of(this.testJobWithActivity));
            when(jobDataManager.saveJobAndActivity(jobArg.capture(), activityArg.capture())).thenAnswer(i -> i.getArgument(0));

            Activity result = underTest.updateActivity(new UpdateActivityCommand(activity.getId(), ActivityType.EMAIL, "activity comment edited", testJobWithActivity.getUserId(), testJobWithActivity.getId()));

            verify(jobDataManager).findByIdAndUserId(testJobWithActivity.getId(), testJobWithActivity.getUserId());
            verify(jobDataManager).saveJobAndActivity(jobArg.capture(), activityArg.capture());

            assertEquals(activity.getId(), result.getId());
            assertEquals(ActivityType.EMAIL, result.getType());
            assertEquals("activity comment edited", result.getComment());
            assertTrue(result.getUpdatedAt().isAfter(before) || result.getUpdatedAt().equals(before));

            Job updatedJob = jobArg.getValue();
            assertNotNull(updatedJob);
            assertEquals(1, updatedJob.getActivities().size());
            assertEquals(result, updatedJob.getActivities().getFirst());
        }

        @Test
        void whenActivityNotFound_thenShouldThrowActivityNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(this.testJobWithActivity));
            var command = new UpdateActivityCommand(ActivityId.generate(), ActivityType.EMAIL, "comment", UserId.generate(), JobId.generate());
            assertThrows(ActivityNotFoundException.class, () -> underTest.updateActivity(command));
        }
    }

    @Nested
    class GetAttachmentFileInfoTest {

        @Test
        void whenJobNotFound_thenShouldThrowJobNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());
            var command = new DownloadAttachmentCommand("someAttachement", UserId.generate(), JobId.generate());
            assertThrows(JobNotFoundException.class, () -> underTest.getAttachmentFileInfo(command));
        }

        @Test
        void whenAttachmentNotFound_thenShouldThrowAttachmentNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(), any())).thenReturn(Optional.of(testJob));
            var command = new DownloadAttachmentCommand("someId'", UserId.generate(), JobId.generate());
            assertThrows(AttachmentNotFoundException.class, () -> underTest.getAttachmentFileInfo(command));
        }

        @Test
        void shouldReturnAttachmentFileInfo() {
            JobId jobId = JobId.generate();
            UserId userId = UserId.generate();

            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(jobId, userId)).thenReturn(Optional.of(testJob));
            when(fileStorage.generateProtectedUrl(testJob.getId(), attachmentId, "fileId")).thenReturn("http://my.url");
            var command = new DownloadAttachmentCommand(attachmentId.toString(), userId, jobId);

            AttachmentFileInfo info = underTest.getAttachmentFileInfo(command);
            assertInstanceOf(AttachmentFileInfo.class, info);

            verify(jobDataManager).findByIdAndUserId(jobId, userId);
            verify(fileStorage).generateProtectedUrl(testJob.getId(), attachmentId, "fileId");

            assertEquals("fileId", info.fileId());
            assertEquals("http://my.url", info.url());
        }
    }


    @Nested
    class UpdateJobRatingTest {

        @Test
        void whenJobNotFound_thenShouldThrowJobNotFoundException() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(JobId.class), any(UserId.class))).thenReturn(Optional.empty());
            UpdateJobRatingCommand command = new UpdateJobRatingCommand(JobId.generate(), UserId.generate(), JobRating.of(2));
            assertThrows(JobNotFoundException.class, () -> underTest.updateJobRating(command));
        }

        @Test
        void shouldUpdateJobRating() {
            reset(userDataManager);
            when(jobDataManager.findByIdAndUserId(any(JobId.class), any(UserId.class))).thenReturn(Optional.of(testJob));
            when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());

            Job updatedJob = underTest.updateJobRating(new UpdateJobRatingCommand(JobId.generate(), UserId.generate(), JobRating.of(2)));

            assertEquals(2, updatedJob.getRating().getValue());
        }
    }


}