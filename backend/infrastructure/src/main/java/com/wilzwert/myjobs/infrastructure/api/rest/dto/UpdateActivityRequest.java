package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for activity update request" )
public class UpdateActivityRequest {

    // TODO @Email(message = "Email should be valid")
    // TODO @Schema(description = "User email")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String comment;
}
