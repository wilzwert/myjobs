package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
// TODO @Schema(description = "Object expected for activity creation request" )
public class CreateActivityRequest {

    // TODO @Schema(description = "")
    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    private ActivityType type;

    private String comment;

}
