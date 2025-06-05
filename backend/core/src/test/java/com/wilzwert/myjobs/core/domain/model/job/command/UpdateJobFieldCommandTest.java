package com.wilzwert.myjobs.core.domain.model.job.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 */

class UpdateJobFieldCommandTest {
    @Test
    void builder_shouldDuplicateUpdateJobFieldCommandCorrectly() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        UpdateJobFieldCommand originalCommand = new UpdateJobFieldCommand(
            jobId,
            userId,
            UpdateJobFieldCommand.Field.TITLE,
            "New title"
        );

        UpdateJobFieldCommand command = new UpdateJobFieldCommand.Builder(originalCommand).build();

        assertEquals(jobId, command.jobId());
        assertEquals(userId, command.userId());
        assertEquals(UpdateJobFieldCommand.Field.TITLE, command.field());
        assertEquals("New title", command.value());
    }

    @Test
    void builder_shouldModifyUpdateJobFieldCommandCorrectly() {
        UserId userId = new UserId(UUID.randomUUID());
        JobId jobId = new JobId(UUID.randomUUID());
        UpdateJobFieldCommand originalCommand = new UpdateJobFieldCommand(
                jobId,
                userId,
                UpdateJobFieldCommand.Field.TITLE,
                "New title"
        );

        UpdateJobFieldCommand command = new UpdateJobFieldCommand.Builder(originalCommand)
                .field(UpdateJobFieldCommand.Field.DESCRIPTION)
                .value("Changed job description")
                .build();

        assertEquals(jobId, command.jobId());
        assertEquals(userId, command.userId());
        assertEquals(UpdateJobFieldCommand.Field.DESCRIPTION, command.field());
        assertEquals("Changed job description", command.value());
    }
}
