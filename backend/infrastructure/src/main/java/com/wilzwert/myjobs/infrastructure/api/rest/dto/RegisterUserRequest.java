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
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @Email(message = "INVALID_EMAIL")
    // TODO @Schema(description = "User email")
    private String email;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    // TODO @Schema(description = "User name")
    private String username;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String password;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String lastName;

    private String lang;


}
