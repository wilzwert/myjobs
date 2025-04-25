package com.wilzwert.myjobs.core.domain.shared.validation;


import java.util.regex.Pattern;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:15:26
 */

public class Validator {

    public static void requireNotEmpty(String fieldName, String fieldValue, ValidationResult validationResult) {
        if(fieldValue.isEmpty()) {
            validationResult.addError(fieldName, ErrorCode.FIELD_CANNOT_BE_EMPTY);
        }
    }

    public static void requireValidEmail(String fieldName, String fieldValue, ValidationResult validationResult) {
        if(!Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$").matcher(fieldValue).matches()) {
            validationResult.addError(fieldName, ErrorCode.INVALID_EMAIL);
        }
    }

}
