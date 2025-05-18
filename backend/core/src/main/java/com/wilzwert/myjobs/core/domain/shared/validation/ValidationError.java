package com.wilzwert.myjobs.core.domain.shared.validation;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:12:07
 */
public class ValidationError {
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
            System.out.println("validation error "+error.code+" to merge has "+error.details.size()+" details");
            if(details == null) {
                System.out.println("local validation error "+error.code+" has NO details");
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
