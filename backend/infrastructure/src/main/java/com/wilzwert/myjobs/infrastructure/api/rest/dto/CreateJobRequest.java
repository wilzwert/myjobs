package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
// FIXME : maybe we should not use hibernate constraint validators
// it is very convenient at the moment, but we should make sure it does not have side effects
// (e.g. dependencies incompatibility...)
import org.hibernate.validator.constraints.URL;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for job creation request" )
public class CreateJobRequest {

    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String title;

    private String company;

    private String profile;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String description;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @URL(message = "INVALID_URL")
    private String url;

    private String salary;

    private String comment;
}
