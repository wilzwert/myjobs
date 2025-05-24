package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */
@Data
// TODO @Schema(description = "Object expected for job login request" )
public class LoginRequest {
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String password;
}
