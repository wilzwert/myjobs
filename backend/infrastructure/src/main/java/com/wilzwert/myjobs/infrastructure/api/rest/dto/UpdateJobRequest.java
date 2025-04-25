package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for job update request" )
public class UpdateJobRequest {
    // TODO @Schema(description = "")
    @NotBlank(message = "The title is required")
    private String title;

    private String company;

    @NotBlank(message = "The URL is required")
    private String url;

    @NotBlank(message = "The description is required")
    private String description;

    private String profile;

    private String salary;
}
