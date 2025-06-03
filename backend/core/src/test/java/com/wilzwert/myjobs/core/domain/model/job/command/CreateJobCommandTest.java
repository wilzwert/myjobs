package com.wilzwert.myjobs.core.domain.model.job.command;

import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Wilhelm Zwertvaegher
 */

class CreateJobCommandTest {
    @Test
    void builder_shouldDuplicateCreateJobCommandCorrectly() {
        UserId userId = new UserId(UUID.randomUUID());
        CreateJobCommand originalCommand = new CreateJobCommand(
                "Job title",
                "Company",
                "https://example.com",
                "Job description",
                "Job profile",
                "Job comment",
                "100k",
                userId
        );

        CreateJobCommand command = new CreateJobCommand.Builder(originalCommand).build();

        assertEquals("Job title", command.title());
        assertEquals("Company", command.company());
        assertEquals("https://example.com", command.url());
        assertEquals("Job description", command.description());
        assertEquals("Job profile", command.profile());
        assertEquals("100k", command.salary());
        assertEquals(userId, command.userId());
    }

    @Test
    void builder_shouldModifyCreateJobCommandCorrectly() {
        UserId userId = new UserId(UUID.randomUUID());
        CreateJobCommand originalCommand = new CreateJobCommand(
                "Job title",
                "Company",
                "https://example.com",
                "Job description",
                "Job profile",
                "Job comment",
                "100k",
                userId
        );

        CreateJobCommand command = new CreateJobCommand.Builder(originalCommand)
                .title("Changed job title")
                .company("Changed company")
                .url("https://example.com/changed")
                .description("Changed description")
                .profile("Changed profile")
                .comment("Changed comment")
                .salary("Changed salary")
                .build();

        assertEquals("Changed job title", command.title());
        assertEquals("Changed company", command.company());
        assertEquals("https://example.com/changed", command.url());
        assertEquals("Changed description", command.description());
        assertEquals("Changed profile", command.profile());
        assertEquals("Changed comment", command.comment());
        assertEquals("Changed salary", command.salary());
        assertEquals(userId, command.userId());
    }
}
