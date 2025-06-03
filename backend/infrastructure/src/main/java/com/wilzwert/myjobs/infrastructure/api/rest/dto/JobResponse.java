package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
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
    private String comment;
    private String salary;
    private JobStatus status;
    private JobRatingResponse rating;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant statusUpdatedAt;
    private List<ActivityResponse> activities;
    private List<AttachmentResponse> attachments;
    // this is a property computed and added by the domain
    private boolean isFollowUpLate;
}
