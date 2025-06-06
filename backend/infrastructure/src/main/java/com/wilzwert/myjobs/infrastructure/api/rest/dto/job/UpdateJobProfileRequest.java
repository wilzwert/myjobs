package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@EqualsAndHashCode(callSuper=true)
// TODO @Schema(description = "Object expected for job profile update request" )
public final class UpdateJobProfileRequest extends UpdateJobFieldRequest {
    private String profile;
}