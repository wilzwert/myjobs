package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.wilzwert.myjobs.core.domain.model.JobRating;
import com.wilzwert.myjobs.core.domain.model.JobStatus;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Component
class JobRatingConverter extends StdConverter<String, JobRating> {

    @Override
    public JobRating convert(String s) {
        return JobRating.of(Integer.parseInt(s));
    }
}

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class UpdateJobRatingRequest {

    // TODO @NotBlank(message = "The email is required")
    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")

    @JsonDeserialize(converter = JobRatingConverter.class)
    private JobRating rating;
}
