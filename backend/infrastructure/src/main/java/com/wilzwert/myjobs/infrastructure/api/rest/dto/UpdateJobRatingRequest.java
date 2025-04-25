package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wilzwert.myjobs.core.domain.model.JobRating;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for job rating update request" )
public class UpdateJobRatingRequest {

    // TODO @Schema(description = "")
    @NotBlank(message = "The rating is required")
    @JsonDeserialize(converter = JobRatingConverter.class)
    private JobRating rating;
}
