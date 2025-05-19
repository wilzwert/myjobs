package com.wilzwert.myjobs.core.domain.shared.validation;


import com.wilzwert.myjobs.core.domain.model.EntityId;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;
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

    private boolean notEmpty(String fieldName, Object fieldValue) {
        if(fieldValue == null
            || fieldValue instanceof String string && string.isEmpty()
            || fieldValue instanceof EntityId<?> entityId && entityId.value() == null) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY));
            return false;
        }
        return true;
    }

    public Validator requireNotNull(String fieldName, Object fieldValue) {
        if(null == fieldValue) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_CANNOT_BE_NULL));
        }
        return this;
    }

    public Validator requireNotEmpty(String fieldName, String fieldValue) {
        notEmpty(fieldName, fieldValue);
        return this;
    }

    public Validator requireNotEmpty(String fieldName, EntityId<?> id) {
        notEmpty(fieldName, id);
        return this;
    }

    public Validator requireValidEmail(String fieldName, String fieldValue) {
        String pattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if(notEmpty(fieldName, fieldValue) && !Pattern.compile(pattern).matcher(fieldValue).matches()) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.INVALID_EMAIL));
        }
        return this;
    }

    public Validator requireValidUrl(String fieldName, String fieldValue) {
        try {
            new URI(fieldValue).toURL();
            // we want at least something like domain.com
            // we have to check it because toURL accepts http://localhost or similar URLS
            if(!fieldValue.contains(".")) {
                throw new MalformedURLException();
            }

        } catch (Exception e) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.INVALID_URL));
        }
        return this;
    }

    public Validator requireMinLength(String fieldName, String fieldValue, int minLength) {
        assert(minLength > 0);
        if(notEmpty(fieldName, fieldValue) && fieldValue.length() < minLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_SHORT));
        }
        return this;
    }

    public Validator requireMaxLength(String fieldName, String fieldValue, int maxLength) {
        assert(maxLength > 0);
        if(notEmpty(fieldName, fieldValue) && fieldValue.length() > maxLength) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_TOO_LONG));
        }
        return this;
    }

    public Validator requireMin(String fieldName, Integer fieldValue, int minValue) {
        if(notEmpty(fieldName, fieldValue) && fieldValue < minValue) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_VALUE_TOO_SMALL, Map.of("min", String.valueOf(minValue))));
        }
        return this;
    }

    public Validator requireMinIfNotNull(String fieldName, Integer fieldValue, int minValue) {
        if(fieldValue == null) {
            return this;
        }
        if(fieldValue < minValue) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_VALUE_TOO_SMALL, Map.of("min", String.valueOf(minValue))));
        }
        return this;
    }

    public Validator requireMax(String fieldName, Integer fieldValue, int maxValue) {
        if(notEmpty(fieldName, fieldValue) && fieldValue > maxValue) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_VALUE_TOO_BIG, Map.of("max", String.valueOf(maxValue))));
        }
        return this;
    }

    public Validator requireMaxIfNotNull(String fieldName, Integer fieldValue, int maxValue) {
        if(fieldValue == null) {
            return this;
        }
        if(fieldValue > maxValue) {
            validationErrors.add(new ValidationError(fieldName, ErrorCode.FIELD_VALUE_TOO_BIG, Map.of("max", String.valueOf(maxValue))));
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