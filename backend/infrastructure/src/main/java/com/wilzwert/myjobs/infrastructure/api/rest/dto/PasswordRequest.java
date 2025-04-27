package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
// TODO @Schema(description = "Object expected for password change request" )
public class PasswordRequest {
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String token;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String oldPassword;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String password;
}
