package com.wilzwert.myjobs.core.domain.shared.validation;


import java.util.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:12:07
 */

public class ValidationResult {
    private final Map<String, List<ValidationError>> errors = new HashMap<>();

    public ValidationResult withError(String field, ErrorCode code) {
        this.addError(field, code);
        return this;
    }

    public void addError(String field, ErrorCode code) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(new ValidationError(field, code));
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Map<String, List<ValidationError>> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    @Override
    public String toString() {
        return "ValidationResult{" + "errors=" + errors + '}';
    }
}
