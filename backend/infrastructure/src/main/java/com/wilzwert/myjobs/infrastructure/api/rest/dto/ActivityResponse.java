package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.wilzwert.myjobs.core.domain.model.ActivityType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:39
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponse {
    private UUID id;
    private String comment;
    private String type;
    private Instant createdAt;
}
