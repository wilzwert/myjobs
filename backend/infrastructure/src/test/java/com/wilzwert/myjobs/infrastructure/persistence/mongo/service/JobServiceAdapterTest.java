package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.activity.Activity;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:09/04/2025
 * Time:14:10
 */
@ExtendWith(MockitoExtension.class)
public class JobServiceAdapterTest {
    @Mock
    private MongoJobRepository mongoJobRepository;

    @Mock
    private JobMapper jobMapper;

    @InjectMocks
    private JobServiceAdapter underTest;

    /**
     * Builds a valid Job to use for tests
     * @param userId a UserId
     * @param jobId the job id
     * @return a valid Job
     */
    private Job getValidTestJob(UserId userId, JobId jobId) {
        return Job.builder()
                .id(jobId)
                .userId(userId)
                .title("title")
                .description("description")
                .url("https://www.example.com")
                .build();
    }


    @Test
    public void whenJobFound_thenShouldReturnMappedJob() {
        JobId jobId = JobId.generate();
        UserId userId = UserId.generate();
        Job job = getValidTestJob(userId, jobId);
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setUserId(userId.value()).setTitle("title").setDescription("description").setUrl("https://www.example.com");

        when(mongoJobRepository.findById(jobId.value())).thenReturn(Optional.of(mongoJob));
        when(jobMapper.toDomain(mongoJob)).thenReturn(job);

        var foundJob = underTest.findById(jobId);

        assert(foundJob.isPresent());
        verify(mongoJobRepository, times(1)).findById(jobId.value());
        verify(jobMapper, times(1)).toDomain(mongoJob);

        Job result = foundJob.get();
        assertEquals(jobId, result.getId());
        assertEquals("https://www.example.com", result.getUrl());
        assertEquals("title", result.getTitle());
        assertEquals("description", result.getDescription());
    }

    @Test
    public void whenJobNotFound_thenShouldReturnEmpty() {
        JobId jobId = JobId.generate();
        when(mongoJobRepository.findById(jobId.value())).thenReturn(Optional.empty());

        assert(underTest.findById(jobId).isEmpty());
    }

    @Test
    public void whenJobNotFoundByUrlAndUserId_thenShouldReturnEmpty() {
        UserId userId = UserId.generate();
        when(mongoJobRepository.findByUrlAndUserId("url", userId.value())).thenReturn(Optional.empty());

        assert(underTest.findByUrlAndUserId("url", userId).isEmpty());
    }

    @Test
    public void whenJobFoundByUrlAndUserId_thenShouldReturnJob() {
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        Job job = getValidTestJob(userId, jobId);
        MongoJob mongoJob = new MongoJob().setUserId(userId.value()).setId(jobId.value()).setTitle("title").setDescription("description").setUrl("https://www.example.com");

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
        assertEquals("description", result.getDescription());
    }

    @Test
    public void whenJobNotFoundByIdAndUserId_thenShouldReturnEmpty() {
        JobId jobId = JobId.generate();
        UserId userId = UserId.generate();
        when(mongoJobRepository.findByIdAndUserId(jobId.value(), userId.value())).thenReturn(Optional.empty());

        assert(underTest.findByIdAndUserId(jobId, userId).isEmpty());
    }

    @Test
    public void whenJobFoundByIdAndUserId_thenShouldReturnJob_() {
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        Job job = getValidTestJob(userId, jobId);
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
    public void whenUserHasNoJob_thenShouldReturnDomainPageWithoutContent_withDefaultArgs() {
        UserId userId = UserId.generate();
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        Page<MongoJob> page = Page.empty();
        List<Job> jobList = Collections.emptyList();

        when(mongoJobRepository.findByUserId(any(UUID.class), argument.capture())).thenReturn(page);
        when(jobMapper.toDomain(page)).thenReturn(DomainPage.builder(jobList).build());

        DomainPage<Job> result = underTest.findAllByUserIdPaginated(userId, 1, 10, null, null);

        assertEquals(0, result.getContent().size());
        verify(mongoJobRepository, times(1)).findByUserId(any(UUID.class), argument.capture());
        verify(jobMapper, times(1)).toDomain(page);
        assertNotNull(argument.getValue().getSort().getOrderFor("createdAt"));
        assertEquals(Sort.Direction.DESC, argument.getValue().getSort().getOrderFor("createdAt").getDirection());
    }

    @Test
    public void whenUserHasJobs_thenShouldReturnDomainPageWithContent_withArgs() {
        UserId userId = UserId.generate();
        ArgumentCaptor<Pageable> argument = ArgumentCaptor.forClass(Pageable.class);
        List<MongoJob> mongoJobs = List.of(
                new MongoJob().setTitle("title"),
                new MongoJob().setTitle("title2")
        );
        Page<MongoJob> page = new PageImpl<>(mongoJobs);

        Job job1 = getValidTestJob(userId, JobId.generate());
        Job job2 = Job.builder()
                .id(JobId.generate())
                .userId(userId)
                .title("title 2")
                .description("description 2")
                .url("https://www.example.com/2")
                .build();

        List<Job> jobs = List.of(job1, job2);

        when(mongoJobRepository.findByUserIdAndStatus(any(UUID.class), any(JobStatus.class), argument.capture())).thenReturn(page);
        when(jobMapper.toDomain(page)).thenReturn(DomainPage.builder(jobs).build());

        DomainPage<Job> result = underTest.findAllByUserIdPaginated(userId, 1, 10, JobStatus.CREATED, "rating,asc");

        assertEquals(2, result.getContent().size());
        verify(mongoJobRepository, times(1)).findByUserIdAndStatus(any(UUID.class), any(JobStatus.class), argument.capture());
        verify(jobMapper, times(1)).toDomain(page);
        assertNull(argument.getValue().getSort().getOrderFor("createdAt"));
        assertNotNull(argument.getValue().getSort().getOrderFor("rating"));
        assertEquals(Sort.Direction.ASC, argument.getValue().getSort().getOrderFor("rating").getDirection());
    }

    @Test
    public void whenSaved_thenShouldReturnJob() {
        JobId jobId = JobId.generate();
        Job jobToSave = getValidTestJob(UserId.generate(), jobId);
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");

        when(jobMapper.toEntity(jobToSave)).thenReturn(mongoJob);
        when(mongoJobRepository.save(mongoJob)).thenReturn(mongoJob);
        when(jobMapper.toDomain(mongoJob)).thenReturn(jobToSave);

        Job result = underTest.save(jobToSave);

        assertEquals(jobId, result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("https://www.example.com", result.getUrl());

        verify(jobMapper, times(1)).toEntity(jobToSave);
        verify(mongoJobRepository, times(1)).save(mongoJob);
        verify(jobMapper, times(1)).toDomain(mongoJob);


    }

    @Test
    public void shouldReturnJob_whenJobAndActivitySaved() {
        JobId jobId = JobId.generate();
        Job jobToSave = getValidTestJob(UserId.generate(), jobId);
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
        Job jobToSave = getValidTestJob(UserId.generate(), jobId);
        Attachment attachment = Attachment.builder().name("attachment").fileId("fileId").filename("file.jpg").contentType("image/jpg").build();
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
        Job jobToDelete = getValidTestJob(UserId.generate(), jobId);
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
        Job job = getValidTestJob(UserId.generate(), jobId);
        MongoJob mongoJob = new MongoJob().setId(jobId.value()).setTitle("title").setUrl("https://www.example.com");
        Attachment attachment = Attachment.builder().name("attachment").fileId("fileId").filename("file.jpg").contentType("image/jpg").build();
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
    }
}
