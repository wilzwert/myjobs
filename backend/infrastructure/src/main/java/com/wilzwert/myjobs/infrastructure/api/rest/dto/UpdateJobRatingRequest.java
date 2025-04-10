package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wilzwert.myjobs.core.domain.model.JobRating;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class UpdateJobRatingRequest {

    // TODO @NotBlank(message = "The email is required")
    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")

    @JsonDeserialize(converter = JobRatingConverter.class)
    private JobRating rating;
}
