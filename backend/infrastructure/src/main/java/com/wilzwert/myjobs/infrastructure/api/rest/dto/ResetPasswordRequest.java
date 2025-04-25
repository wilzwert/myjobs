package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for password reset request" )
public class ResetPasswordRequest {
    // TODO @Schema(description = "")
    @NotBlank(message = "The email is required")
    private String email;
}
