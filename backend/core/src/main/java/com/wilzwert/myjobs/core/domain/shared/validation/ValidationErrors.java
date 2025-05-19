package com.wilzwert.myjobs.core.domain.shared.validation;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ValidationErrors implements Serializable {
    private final Map<String, Map<ErrorCode, ValidationError>> errors = new LinkedHashMap<>();

    public void add(ValidationError error) {
        errors.computeIfAbsent(error.field(), k -> new LinkedHashMap<>());
        addValidationError(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<String, List<ValidationError>> getErrors() {
        return Collections.unmodifiableMap(errors.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new ArrayList<>(e.getValue().values())
                ))
        );
    }

    private Map<String, Map<ErrorCode, ValidationError>> getErrorsMap() {
        return errors;
    }

    private void addValidationError(ValidationError error) {
        // merge the ValidationError to keep details if provided
        if(errors.get(error.field()).containsKey(error.code())) {
            errors.get(error.field()).get(error.code()).merge(error);
        }
        else {
            errors.get(error.field()).put(error.code(), error);
        }
    }

    public void merge(ValidationErrors validationErrors) {
        validationErrors.getErrorsMap().forEach((fieldName, e) -> {
            // init Map of ValidationError for the field if not present
            errors.computeIfAbsent(fieldName, l -> new LinkedHashMap<>());

            e.values().forEach(this::addValidationError);
        });
    }

    @Override
    public String toString() {
        return "ValidationErrors{" + "errors=" + errors + '}';
    }
}
