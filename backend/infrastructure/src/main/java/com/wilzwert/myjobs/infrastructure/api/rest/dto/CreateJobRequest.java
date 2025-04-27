package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for job creation request" )
public class CreateJobRequest {

    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String title;

    private String company;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String url;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String description;

    private String profile;

    private String salary;
}
