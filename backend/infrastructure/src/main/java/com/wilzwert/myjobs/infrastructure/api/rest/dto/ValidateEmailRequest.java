package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for email validation request" )
public class ValidateEmailRequest {
    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String code;
}
