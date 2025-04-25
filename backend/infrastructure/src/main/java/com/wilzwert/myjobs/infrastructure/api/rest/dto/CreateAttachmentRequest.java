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
    @NotBlank(message = "The attachment name is required")
    private String name;

    @NotBlank(message = "The file name is required")
    private String filename;

    @NotBlank(message = "The file extension is required")
    private String extension;

    @NotBlank(message = "The file content is required")
    private String content;

}
