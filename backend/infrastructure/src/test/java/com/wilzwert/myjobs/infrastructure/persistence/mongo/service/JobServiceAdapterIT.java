package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.JobId;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:14:10
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class JobServiceAdapterIT extends AbstractBaseIntegrationTest {

    @Autowired
    private JobServiceAdapter underTest;

    @Test
    public void shouldReturnMappedJob_whenJobFound() {
        /*
        JobId jobId = JobId.generate();
        Job job = Job.builder().id(jobId).title("title").url("https://www.example.com").build();
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(mongoJobRepository.findById(jobId.value())).thenReturn(Optional.of(mongoJob));
        when(jobMapper.toDomain(mongoJob)).thenReturn(job);

        var foundJob = underTest.findById(jobId);

        assert(foundJob.isPresent());
        verify(mongoJobRepository, times(1)).findById(jobId.value());
        verify(jobMapper, times(1)).toDomain(mongoJob);

        Job result = foundJob.get();
        assertEquals(jobId, result.getId());
        assertEquals("https://www.example.com", result.getUrl());
        assertEquals("title", result.getTitle());*/
    }
    /*
    @Test
    public void shouldReturnEmpty_whenJobNotFound() {
        JobId jobId = JobId.generate();
        when(mongoJobRepository.findById(jobId.value())).thenReturn(Optional.empty());

        assert(underTest.findById(jobId).isEmpty());
    }

    @Test
    public void shouldReturnEmpty_whenJobNotFoundByUrlAndUserId() {
        UserId userId = UserId.generate();
        when(mongoJobRepository.findByUrlAndUserId("url", userId.value())).thenReturn(Optional.empty());

        assert(underTest.findByUrlAndUserId("url", userId).isEmpty());
    }

    @Test
    public void shouldReturnJob_whenJobFoundByUrlAndUserId() {
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        Job job = Job.builder().id(jobId).title("title").url("https://www.example.com").userId(userId).build();
        MongoJob mongoJob = new MongoJob().setUserId(userId.value()).setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(mongoJobRepository.findByUrlAndUserId("https://www.example.com", userId.value())).thenReturn(Optional.of(mongoJob));
        when(jobMapper.toDomain(mongoJob)).thenReturn(job);

        var foundJob = underTest.findByUrlAndUserId("https://www.example.com", userId);

        assert(foundJob.isPresent());
        verify(mongoJobRepository, times(1)).findByUrlAndUserId("https://www.example.com", userId.value());
        verify(jobMapper, times(1)).toDomain(mongoJob);

        Job result = foundJob.get();
        assertEquals(jobId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("https://www.example.com", result.getUrl());
        assertEquals("title", result.getTitle());
    }

    @Test
    public void shouldReturnEmpty_whenJobNotFoundByIdAndUserId() {
        JobId jobId = JobId.generate();
        UserId userId = UserId.generate();
        when(mongoJobRepository.findByIdAndUserId(jobId.value(), userId.value())).thenReturn(Optional.empty());

        assert(underTest.findByIdAndUserId(jobId, userId).isEmpty());
    }

    @Test
    public void shouldReturnJob_whenJobFoundByIdAndUserId() {
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        Job job = Job.builder().id(jobId).title("title").url("https://www.example.com").userId(userId).build();
        MongoJob mongoJob = new MongoJob().setUserId(userId.value()).setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(mongoJobRepository.findByIdAndUserId(jobId.value(), userId.value())).thenReturn(Optional.of(mongoJob));
        when(jobMapper.toDomain(mongoJob)).thenReturn(job);

        var foundJob = underTest.findByIdAndUserId(jobId, userId);

        assert(foundJob.isPresent());
        verify(mongoJobRepository, times(1)).findByIdAndUserId(jobId.value(), userId.value());
        verify(jobMapper, times(1)).toDomain(mongoJob);

        Job result = foundJob.get();
        assertEquals(jobId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("https://www.example.com", result.getUrl());
        assertEquals("title", result.getTitle());
    }

    @Test
    public void shouldReturnDomainPageWithoutContent_whenUserHasNoJob_withDefaultArgs() {
        UserId userId = UserId.generate();
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        Page<MongoJob> page = Page.empty();
        List<Job> jobList = Collections.emptyList();

        when(mongoJobRepository.findByUserId(any(UUID.class), argument.capture())).thenReturn(page);
        when(jobMapper.toDomain(page)).thenReturn(DomainPage.builder(jobList).build());

        DomainPage<Job> result = underTest.findAllByUserId(userId, 1, 10, null, null);

        assertEquals(0, result.getContent().size());
        verify(mongoJobRepository, times(1)).findByUserId(any(UUID.class), argument.capture());
        verify(jobMapper, times(1)).toDomain(page);
        assertNotNull(argument.getValue().getSort().getOrderFor("createdAt"));
        assertEquals(Sort.Direction.DESC, argument.getValue().getSort().getOrderFor("createdAt").getDirection());
    }

    @Test
    public void shouldReturnDomainPageWithContent_whenUserHasJobs_withArgs() {
        UserId userId = UserId.generate();
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        List<MongoJob> mongoJobs = List.of(
                new MongoJob().setTitle("job 1"),
                new MongoJob().setTitle("job 2")
        );
        Page<MongoJob> page = new PageImpl<>(mongoJobs);
        List<Job> jobs = List.of(
                Job.builder().title("job 1").build(),
                Job.builder().title("job 2").build()
        );

        when(mongoJobRepository.findByUserIdAndStatus(any(UUID.class), any(JobStatus.class), argument.capture())).thenReturn(page);
        when(jobMapper.toDomain(page)).thenReturn(DomainPage.builder(jobs).build());

        DomainPage<Job> result = underTest.findAllByUserId(userId, 1, 10, JobStatus.CREATED, "rating,asc");

        assertEquals(2, result.getContent().size());
        verify(mongoJobRepository, times(1)).findByUserIdAndStatus(any(UUID.class), any(JobStatus.class), argument.capture());
        verify(jobMapper, times(1)).toDomain(page);
        assertNull(argument.getValue().getSort().getOrderFor("createdAt"));
        assertNotNull(argument.getValue().getSort().getOrderFor("rating"));
        assertEquals(Sort.Direction.ASC, argument.getValue().getSort().getOrderFor("rating").getDirection());
    }*/

    @Test
    public void shouldCreateJob_andRetrieveCreatedJob() {

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

        System.out.println(result);

        assertEquals(jobId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("https://www.example.com", result.getUrl());

        underTest.findById(jobId)
        .ifPresentOrElse((job) -> {
            assertEquals(jobId, result.getId());
            assertEquals("title", result.getTitle());
            assertEquals("https://www.example.com", result.getUrl());
        },
        () -> fail("Job should be retrievable after saving"));
    }
    /*
    @Test
    public void shouldReturnJob_whenJobAndActivitySaved() {
        JobId jobId = JobId.generate();
        Job jobToSave = Job.builder().id(jobId).title("title").url("https://www.example.com").build();
        Activity activity = Activity.builder().type(ActivityType.APPLICATION).comment("application").build();

        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(jobMapper.toEntity(jobToSave)).thenReturn(mongoJob);
        when(mongoJobRepository.save(mongoJob)).thenReturn(mongoJob);
        when(jobMapper.toDomain(mongoJob)).thenReturn(jobToSave);

        Job result = underTest.saveJobAndActivity(jobToSave, activity);

        assertEquals(jobId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("https://www.example.com", result.getUrl());

        verify(jobMapper, times(1)).toEntity(jobToSave);
        verify(mongoJobRepository, times(1)).save(mongoJob);
        verify(jobMapper, times(1)).toDomain(mongoJob);
    }

    @Test
    public void shouldReturnJob_whenJobAndAttachmentSaved() {
        JobId jobId = JobId.generate();
        Job jobToSave = Job.builder().id(jobId).title("title").url("https://www.example.com").build();
        Attachment attachment = Attachment.builder().name("attachment").build();
        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_CREATION).comment("attachment").build();

        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(jobMapper.toEntity(jobToSave)).thenReturn(mongoJob);
        when(mongoJobRepository.save(mongoJob)).thenReturn(mongoJob);
        when(jobMapper.toDomain(mongoJob)).thenReturn(jobToSave);

        Job result = underTest.saveJobAndAttachment(jobToSave, attachment, activity);

        assertEquals(jobId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("https://www.example.com", result.getUrl());

        verify(jobMapper, times(1)).toEntity(jobToSave);
        verify(mongoJobRepository, times(1)).save(mongoJob);
        verify(jobMapper, times(1)).toDomain(mongoJob);
    }

    @Test
    public void shouldDeleteJob() {
        JobId jobId = JobId.generate();
        Job jobToDelete = Job.builder().id(jobId).title("title").url("https://www.example.com").build();
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(jobMapper.toEntity(jobToDelete)).thenReturn(mongoJob);
        doNothing().when(mongoJobRepository).delete(any());

        underTest.delete(jobToDelete);

        verify(jobMapper, times(1)).toEntity(jobToDelete);
        verify(mongoJobRepository, times(1)).delete(mongoJob);
    }

    @Test
    public void shouldDeleteAttachment() {
        JobId jobId = JobId.generate();
        Job job = Job.builder().id(jobId).title("title").url("https://www.example.com").build();
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");
        Attachment attachment = Attachment.builder().name("attachment").build();
        Activity activity = Activity.builder().type(ActivityType.ATTACHMENT_DELETION).comment("attachment").build();

        when(jobMapper.toEntity(job)).thenReturn(mongoJob);
        when(mongoJobRepository.save(mongoJob)).thenReturn(mongoJob);
        when(jobMapper.toDomain(mongoJob)).thenReturn(job);

        Job updateJob = underTest.deleteAttachment(job, attachment, activity);

        verify(jobMapper, times(1)).toEntity(job);
        verify(mongoJobRepository, times(1)).save(mongoJob);
        verify(jobMapper, times(1)).toDomain(mongoJob);
        assertEquals(jobId, updateJob.getId());
        assertEquals("title", updateJob.getTitle());
        assertEquals("https://www.example.com", updateJob.getUrl());
    }*/
}
