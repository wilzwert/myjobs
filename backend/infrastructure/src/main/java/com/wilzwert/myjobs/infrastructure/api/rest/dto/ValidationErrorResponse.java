package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {
    @NonNull
    private String code;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, String> details;
}