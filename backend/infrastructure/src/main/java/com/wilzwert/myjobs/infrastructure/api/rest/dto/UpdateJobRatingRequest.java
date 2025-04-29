package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for job rating update request" )
public class UpdateJobRatingRequest {

    // TODO @Schema(description = "")
    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    @JsonDeserialize(converter = JobRatingConverter.class)
    // this will be used for testing with MockMvc because otherwise ObjectMapper ie unable to serialize the jobrating
    @JsonSerialize( converter = JobRatingSerializer.class)
    private JobRating rating;
}
