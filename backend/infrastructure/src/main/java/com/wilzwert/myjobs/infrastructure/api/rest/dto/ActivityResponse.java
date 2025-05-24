package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponse {
    private UUID id;
    private String comment;
    private ActivityType type;
    private Instant createdAt;
}
