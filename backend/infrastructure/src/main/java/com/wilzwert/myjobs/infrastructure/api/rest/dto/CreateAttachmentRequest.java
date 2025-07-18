package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for attachment creation request" )
public class CreateAttachmentRequest {
    // TODO @Schema(description = "Attachment name")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String name;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String filename;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String content;
}
