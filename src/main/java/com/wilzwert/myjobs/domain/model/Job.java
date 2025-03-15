package com.wilzwert.myjobs.domain.model;


import com.wilzwert.myjobs.domain.command.CreateJobCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:32
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    private UUID id;

    private String url;

    private JobStatus status;

    private String title;

    private String description;

    private String profile;

    private Instant createdAt;

    private Instant updatedAt;

    private UUID userId;

    public static Job fromCommand(CreateJobCommand command, User user) {
        return new Job(
                UUID.randomUUID(),
                command.url(),
                JobStatus.CREATED,
                command.title(),
                "",
                "",
                null,
                null,
                user.getId()
        );
    }
}
