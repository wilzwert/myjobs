package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class RegisterUserRequest {
    @NotBlank(message = "The email is required")
    @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")
    private String email;

    @NotBlank(message = "The username is required")
    // TODO @Schema(description = "User name")
    private String username;

    @NotBlank(message = "The password is required")
    private String password;

    @NotBlank(message = "The first name is required")
    private String firstName;

    @NotBlank(message = "The last name is required")
    private String lastName;
}
