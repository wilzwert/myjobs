package com.wilzwert.myjobs.core.domain.shared.validation;


import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.net.URI;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:15:26
 */

public class Validator {

    private final ValidationErrors validationErrors;

    public Validator() {
        validationErrors = new ValidationErrors();
    }

    public ValidationErrors getErrors() {
        return validationErrors;
    }

    public Validator requireNotEmpty(String fieldName, String fieldValue) {
        if(fieldValue == null || fieldValue.isEmpty()) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        return this;
    }

    public Validator requireNotEmpty(String fieldName, EntityId<?> id) {
        if(id == null || id.value() == null) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        return this;
    }

    public Validator requireValidEmail(String fieldName, String fieldValue) {
        if(fieldValue == null || fieldValue.isEmpty()) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.INVALID_EMAIL));
        }
        else if(!Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$").matcher(fieldValue).matches()) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.INVALID_EMAIL));
        }
        return this;
    }

    public Validator requireValidUrl(String fieldName, String fieldValue) {
        try {
            new URI(fieldValue).toURL();

        } catch (Exception e) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.INVALID_URL));
        }
        return this;
    }

    public Validator requireMinLength(String fieldName, String fieldValue, int minLength) {
        if (fieldValue == null || fieldValue.length() < minLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_SHORT));
        }
        return this;
    }

    public Validator requireMaxLength(String fieldName, String fieldValue, int maxLength) {
        if (fieldValue == null || fieldValue.length() > maxLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_LONG));
        }
        return this;
    }

    public Validator require(String fieldName, BooleanSupplier supplier, ErrorCode errorCode) {
        if(!supplier.getAsBoolean()) {
            validationErrors.add(new ValidationError(fieldName, errorCode));
        }
        return this;
    }
}