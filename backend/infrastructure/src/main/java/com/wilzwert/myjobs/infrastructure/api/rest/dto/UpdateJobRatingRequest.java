package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for job rating update request" )
public class UpdateJobRatingRequest {

    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @JsonDeserialize(converter = JobRatingConverter.class)
    private JobRating rating;
}
