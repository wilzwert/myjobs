package com.wilzwert.myjobs.core.domain.shared.validation;

import java.util.*;

public class ValidationErrors {
    private final Map<String, List<ValidationError>> errors = new HashMap<>();

    public void add(ValidationError error) {
        errors.computeIfAbsent(error.field(), k -> new ArrayList<>()).add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, List<ValidationError>> getErrors() {
        return Collections.unmodifiableMap(errors);
    }

    public void merge(ValidationErrors validationErrors) {
        errors.putAll(validationErrors.getErrors());
    }

    @Override
    public String toString() {
        return "ValidationErrors{" + "errors=" + errors + '}';
    }
}
