package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.wilzwert.myjobs.core.domain.model.ActivityType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Data
// TODO @Schema(description = "Object expected for activity creation request" )
public class CreateActivityRequest {

    // TODO @Schema(description = "")
    @NotBlank(message = "The activity type is required")
    private ActivityType type;

    private String comment;

}
