package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for password change request" )
public class PasswordRequest {
    @NotBlank(message = "The token is required")
    private String token;

    @NotBlank(message = "The old password is required")
    private String oldPassword;

    @NotBlank(message = "The password is required")
    private String password;
}
