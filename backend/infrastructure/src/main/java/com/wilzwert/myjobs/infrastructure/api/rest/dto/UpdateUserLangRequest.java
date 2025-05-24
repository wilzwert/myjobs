package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wilzwert.myjobs.core.domain.model.user.Lang;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Wilhelm Zwertvaegher
 */

@Data
@AllArgsConstructor
// TODO @Schema(description = "Object expected for user update request" )
public class UpdateUserLangRequest {
    // TODO @Schema(description = "Lang fr or en")
    @NotNull(message = "FIELD_CANNOT_BE_EMPTY")
    @JsonDeserialize(converter = LangConverter.class)
    private Lang lang;
}
