package com.wilzwert.myjobs.core.domain.shared.validation;


import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:12:07
 */
public record ValidationError(String field, ErrorCode code, Map<String, String> details) {
    public ValidationError(String field, ErrorCode code) {
        this(field, code, null);
    }
}
