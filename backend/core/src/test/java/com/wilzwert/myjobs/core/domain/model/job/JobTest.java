package com.wilzwert.myjobs.core.domain.model.job;


import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:16:45
 */

public class JobTest {

    @Test
    public void shouldCreateJob() {
        UserId userId = new UserId(UUID.randomUUID());
        Job job = Job.builder()
                .url("http://www.example.com")
                .title("Job title")
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .build();

        assertNotNull(job);
        assertEquals("Job title", job.getTitle());
        assertEquals("http://www.example.com", job.getUrl());
        assertEquals("Job company", job.getCompany());
        assertEquals("Job description", job.getDescription());
        assertEquals("Job profile", job.getProfile());
        assertEquals("TBD", job.getSalary());
        assertEquals(userId.value(), job.getUserId().value());
    }

    @Test
    public void shouldCreateJobFromOtherJob() {
        UserId userId = new UserId(UUID.randomUUID());
        Job job = Job.builder()
                .url("http://www.example.com")
                .title("Job title")
                .company("Job company")
                .description("Job description")
                .profile("Job profile")
                .salary("TBD")
                .userId(userId)
                .build();

        Job otherJob = Job.from(job).build();

        assertNotNull(otherJob);
        assertEquals("Job title", otherJob.getTitle());
        assertEquals("http://www.example.com", otherJob.getUrl());
        assertEquals("Job company", otherJob.getCompany());
        assertEquals("Job description", otherJob.getDescription());
        assertEquals("Job profile", otherJob.getProfile());
        assertEquals("TBD", otherJob.getSalary());
        assertEquals(userId.value(), otherJob.getUserId().value());
    }
}
