package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for user update request" )
public class UpdateUserRequest {
    // TODO @Schema(description = "User email")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @Email(message = "INVALID_EMAIL")
    private String email;

    // TODO @Schema(description = "User name")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String username;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String lastName;

    private Integer jobFollowUpReminderDays;
}
