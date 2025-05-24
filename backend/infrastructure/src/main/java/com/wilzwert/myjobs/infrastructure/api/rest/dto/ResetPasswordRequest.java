package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for password reset request" )
public class ResetPasswordRequest {
    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @Email(message = "INVALID_EMAIL")
    private String email;
}
