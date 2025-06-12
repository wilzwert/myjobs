package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 */
public class JobDataManagerAdapterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private JobDataManagerAdapter underTest;

    @Test
    void shouldCreateJob_andRetrieveCreatedJob() {
        JobId jobId = JobId.generate();
        UserId userId = UserId.generate();
        Job jobToSave = Job.builder()
                .userId(userId)
                .id(jobId)
                .title("title")
                .company("company")
                .url("https://www.example.com")
                .description("description")
                .profile("profile")
                .salary("none")
                .build();

        Job result = underTest.save(jobToSave);

        assertEquals(jobId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("https://www.example.com", result.getUrl());

        underTest.findById(jobId)
        .ifPresentOrElse((job) -> {
            assertEquals(jobId, job.getId());
            assertEquals("title", job.getTitle());
            assertEquals("https://www.example.com", job.getUrl());
        },
        () -> fail("Job should be retrievable after saving"));
    }

    @Test
    void shouldRetrieveJobToBeReminded() {
        Map<JobId, Job> jobs = underTest.findMinimal(DomainSpecification.JobFollowUpToRemind(Instant.now()));
        assertNotNull(jobs);
        assertThat(jobs).hasSize(3);
    }

    @Test
    void shouldRetrieveMinimalJobs() {
        // get 2 known jobs
        JobId jobId1 = new JobId(UUID.fromString("77777777-7777-7777-7777-123456789012"));
        JobId jobId2 = new JobId(UUID.fromString("88888888-8888-8888-8888-123456789012"));

        DomainSpecification spec = DomainSpecification.applySort(
                DomainSpecification.in("id", List.of(jobId1, jobId2)),
                DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
        );
        Map<JobId, Job> jobs = underTest.findMinimal(spec);
        assertNotNull(jobs);
        assertThat(jobs).hasSize(2);

        // we know that for the jobs collection, we always load complete aggregates because
        // needed "relations" in domain are implemented as nested collections in infra with mongo
        assertDoesNotThrow(() -> jobs.get(jobId1).getActivities());
        assertDoesNotThrow(() -> jobs.get(jobId2).getActivities());
    }


    @Test
    void shouldSaveAllJobs() {
        // get 2 known jobs
        JobId jobId1 = new JobId(UUID.fromString("77777777-7777-7777-7777-123456789012"));
        JobId jobId2 = new JobId(UUID.fromString("88888888-8888-8888-8888-123456789012"));

        DomainSpecification spec = DomainSpecification.applySort(
                DomainSpecification.in("id", List.of(jobId1, jobId2)),
                DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
        );

        Map<JobId, Job> jobs = underTest.findMinimal(spec);
        assertThat(jobs).hasSize(2);

        String stringDate = "09:15:30, 10/05/2025";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd/MM/yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(stringDate, formatter);
        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        Instant newInstant = zonedDateTime.toInstant();

        // update jobFollowUpReminderSentAt for both
        Set<Job> jobsToSave = new HashSet<>();
        jobsToSave.add(Job.fromMinimal(jobs.get(jobId1)).title("new title").followUpReminderSentAt(newInstant).build());
        jobsToSave.add(Job.fromMinimal(jobs.get(jobId2)).title("new title 2").followUpReminderSentAt(newInstant).build());

        // when
        BulkDataSaveResult result = underTest.saveAll(jobsToSave);

        assertThat(result).isNotNull();
        assertThat(result.totalCount()).isEqualTo(2);
        assertThat(result.updatedCount()).isEqualTo(2);

        // check that only jobFollowUpReminderSentAt has been saved, as it is the only property supported for bulk updates
        // original followUpReminderSentAt | title (see resources/test-data/jobs.json)
        // job1 "followUpReminderSentAt": "2025-05-04T15:25:31.162Z" | My job,
        // job2 null | My second job
        Map<JobId, Job> usersReloaded = underTest.findMinimal(spec);
        assertThat(usersReloaded).hasSize(2);
        assertThat(usersReloaded.get(jobId1).getTitle()).isEqualTo("My job");
        assertThat(usersReloaded.get(jobId1).getFollowUpReminderSentAt()).isEqualTo(newInstant);
        assertThat(usersReloaded.get(jobId2).getTitle()).isEqualTo("My second job");
        assertThat(usersReloaded.get(jobId2).getFollowUpReminderSentAt()).isEqualTo(newInstant);


        // reset original jobs to ensure tests further consistency and predictability
        // (which in itself could be considered another similar saveAll test)
        // actually we just have to save the jobs loaded at the beginning of this test
        // because they remain at their original state, as all the other methods worked on copies
        Set<Job> jobsToReset = new HashSet<>();
        jobsToReset.add(jobs.get(jobId1));
        jobsToReset.add(jobs.get(jobId2));

        BulkDataSaveResult result2 = underTest.saveAll(jobsToReset);

        assertThat(result2).isNotNull();
        assertThat(result2.totalCount()).isEqualTo(2);
        assertThat(result2.updatedCount()).isEqualTo(2);
    }

    @Test
    void whenUsersSetEmpty_thenSaveShouldThrowException() {
        // given
        var emptySet = Collections.<Job>emptySet();

        // when + then
        assertThrows(IllegalArgumentException.class, () -> underTest.saveAll(emptySet));
    }

    @Test
    void shouldStreamJobs() {
        // get 2 known jobs
        JobId jobId1 = new JobId(UUID.fromString("77777777-7777-7777-7777-123456789012"));
        JobId jobId2 = new JobId(UUID.fromString("88888888-8888-8888-8888-123456789012"));

        DomainSpecification spec = DomainSpecification.applySort(
                DomainSpecification.in("id", List.of(jobId1, jobId2)),
                DomainSpecification.sort("id", DomainSpecification.SortDirection.ASC)
        );

        Stream<Job> stream = underTest.stream(spec);

        // collect the Stream into a Map to be able to check contents
        Map<JobId, Job> result = stream.collect(Collectors.toMap(Job::getId, e -> e));
        assertThat(result).hasSize(2);
        assertThat(result.get(jobId1).getTitle()).isEqualTo("My job");
        assertThat(result.get(jobId2).getTitle()).isEqualTo("My second job");
    }
}
