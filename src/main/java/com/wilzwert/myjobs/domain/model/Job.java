package com.wilzwert.myjobs.domain.model;


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
}
