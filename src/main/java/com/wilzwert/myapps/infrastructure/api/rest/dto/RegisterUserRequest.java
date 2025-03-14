package com.wilzwert.myapps.infrastructure.api.rest.dto;


import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class RegisterUserRequest {
    // TODO @NotBlank(message = "The email is required")
    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")
    private String email;

    // TODO @NotBlank(message = "The name is required")
    // TODO @Schema(description = "User name")
    private String username;

    // TODO @NotBlank(message = "The password is required")
    // TODO @Schema(description = "User password")
    private String password;

    private String firstName;

    private String lastName;
}
