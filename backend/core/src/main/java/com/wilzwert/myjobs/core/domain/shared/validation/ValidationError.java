package com.wilzwert.myjobs.core.domain.shared.validation;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 */
public class ValidationError implements Serializable {
    private final String field;
    private final ErrorCode code;

    private Map<String, String> details;

    public ValidationError(String field, ErrorCode code, Map<String, String> details) {
        this.field = field;
        this.code = code;
        this.details = (details != null ? new LinkedHashMap<>(details) : null);
    }

    public ValidationError(String field, ErrorCode code) {
        this(field, code, null);
    }

    public void merge(ValidationError error) {
        if(error.details != null) {
            if(details == null) {
                details = new HashMap<>();
            }
            details.putAll(error.details);
        }
    }

    public String field() {
        return field;
    }

    public ErrorCode code() {
        return code;
    }

    public Map<String, String> details() {
        return details;
    }
}
