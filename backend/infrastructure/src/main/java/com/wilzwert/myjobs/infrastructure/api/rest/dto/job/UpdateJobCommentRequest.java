package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@EqualsAndHashCode(callSuper=true)
// TODO @Schema(description = "Object expected for job comment update request" )
public final class UpdateJobCommentRequest extends UpdateJobFieldRequest {
    private String comment;
}