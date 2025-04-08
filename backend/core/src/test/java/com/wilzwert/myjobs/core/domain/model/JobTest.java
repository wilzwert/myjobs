package com.wilzwert.myjobs.core.domain.model;


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
        Job job = Job.create(
                "http://www.example.com",
                "Job title",
                "Job company",
                "Job description",
                "Job profile",
                "TBD",
                userId
        );

        assertNotNull(job);
        assertEquals("Job title", job.getTitle());
        assertEquals("Job company", job.getCompany());
        assertEquals("Job description", job.getDescription());
        assertEquals("Job profile", job.getProfile());
        assertEquals("TBD", job.getSalary());
        assertEquals(userId.value(), job.getUserId().value());
    }
}
