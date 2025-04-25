package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:18:31
 */
@Data
// TODO @Schema(description = "Object expected for job login request" )
public class LoginRequest {
    @NotBlank(message = "The email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "The password is required")
    private String password;
}
