package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for job status update request" )
public class UpdateJobStatusRequest {
    // TODO @Schema(description = "")
    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    private JobStatus status;
}
