package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@EqualsAndHashCode(callSuper=true)
// TODO @Schema(description = "Object expected for job title update request" )
public final class UpdateJobTitleRequest extends UpdateJobFieldRequest {
    // TODO @Schema(description = "")
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    private String title;
}
