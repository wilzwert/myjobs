package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@EqualsAndHashCode(callSuper=true)
// TODO @Schema(description = "Object expected for job url update request" )
public final class UpdateJobUrlRequest extends UpdateJobFieldRequest {
    @NotBlank(message = "FIELD_CANNOT_BE_EMPTY")
    @URL(message = "INVALID_URL")
    private String url;
}
