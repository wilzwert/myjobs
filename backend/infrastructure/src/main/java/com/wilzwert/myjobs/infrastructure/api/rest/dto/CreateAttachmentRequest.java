package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
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
    private String extension;

    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String content;

}
