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
        // String pattern  ="^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        /*String pattern =  "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,7})$";*/
        String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if(fieldValue == null || fieldValue.isEmpty()) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        else if(!Pattern.compile(pattern).matcher(fieldValue).matches()) {
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
        assert(minLength > 0);
        if (fieldValue == null) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        else if(fieldValue.length() < minLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_SHORT));
        }
        return this;
    }

    public Validator requireMaxLength(String fieldName, String fieldValue, int maxLength) {
        assert(maxLength > 0);
        if (fieldValue == null) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        else if(fieldValue.length() > maxLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_LONG));
        }
        return this;
    }

    public Validator requireMinMaxLength(String fieldName, String fieldValue, int minLength, int maxLength) {
        assert(minLength > 0 && maxLength > 0 && maxLength > minLength);
        if (fieldValue == null) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
        }
        else if(fieldValue.length() < minLength || fieldValue.length() > maxLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_MIN_MAX_LENGTH));
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