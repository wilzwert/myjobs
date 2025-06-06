package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for job update request" )
public final class UpdateJobRequest implements UpdateJobDto {
    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String title;

    private String company;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @URL(message = "INVALID_URL")
    private String url;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String description;

    private String profile;

    private String comment;

    private String salary;
}
