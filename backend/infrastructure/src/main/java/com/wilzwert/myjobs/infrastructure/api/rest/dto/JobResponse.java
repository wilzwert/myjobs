package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.wilzwert.myjobs.core.domain.model.JobRating;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobRatingMapper;
import lombok.*;

import java.time.Instant;
import java.util.List;
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
public class JobResponse {
    private UUID id;
    private String title;
    private String company;
    private String url;
    private String description;
    private String profile;
    private JobStatus status;
    private JobRatingResponse rating;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ActivityResponse> activities;
    private List<AttachmentResponse> attachments;
}
