package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for job status update request" )
public final class UpdateJobStatusRequest implements UpdateJobDto {
    // TODO @Schema(description = "")
    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    private JobStatus status;
}