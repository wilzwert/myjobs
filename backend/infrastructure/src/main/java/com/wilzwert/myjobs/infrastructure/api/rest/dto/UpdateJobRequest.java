package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class UpdateJobRequest {
    // TODO @NotBlank(message = "The email is required")
    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")
    private String title;

    private String company;

    private String url;

    private String description;

    private String profile;

    private String salary;
}
