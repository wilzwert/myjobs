package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.ActivityType;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for user registration request" )
public class CreateActivityRequest {
    // TODO @NotBlank(message = "The email is required")
    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")
    private ActivityType type;

    private String comment;

}
