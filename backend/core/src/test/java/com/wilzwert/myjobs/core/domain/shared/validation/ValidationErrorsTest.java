package com.wilzwert.myjobs.core.domain.shared.validation;


import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 */

class ValidationErrorsTest {
    @Test
    void shouldAddValidationError() {
        ValidationErrors errors = new ValidationErrors();
        assertEquals(0, errors.getErrors().size());

        errors.add(new ValidationError("field", ErrorCode.INCOMPLETE_AGGREGATE));
        assertEquals(1, errors.getErrors().size());
        assertEquals(ErrorCode.INCOMPLETE_AGGREGATE, errors.getErrors().get("field").getFirst().code());
    }

    @Test
    void shouldAddValidationErrors() {
        ValidationErrors errors = new ValidationErrors();
        assertEquals(0, errors.getErrors().size());

        errors.add(new ValidationError("field", ErrorCode.FIELD_CANNOT_BE_EMPTY));
        errors.add(new ValidationError("field", ErrorCode.JOB_NOT_FOUND));
        errors.add(new ValidationError("field2", ErrorCode.FIELD_VALUE_TOO_BIG));
        errors.add(new ValidationError("field2", ErrorCode.UNEXPECTED_ERROR));
        errors.add(new ValidationError("field3", ErrorCode.USER_NOT_FOUND));

        assertEquals(3, errors.getErrors().size());
        var fieldErrors = errors.getErrors().get("field");
        assertEquals(2, fieldErrors.size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, fieldErrors.getFirst().code());
        assertEquals(ErrorCode.JOB_NOT_FOUND, fieldErrors.get(1).code());

        var field2Errors = errors.getErrors().get("field2");
        assertEquals(2, field2Errors.size());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_BIG, field2Errors.getFirst().code());
        assertEquals(ErrorCode.UNEXPECTED_ERROR, field2Errors.get(1).code());

        assertEquals(ErrorCode.USER_NOT_FOUND, errors.getErrors().get("field3").getFirst().code());
    }

    @Test
    void shouldMergeValidationErrors() {
        ValidationErrors errors = new ValidationErrors();
        errors.add(new ValidationError("field", ErrorCode.FIELD_CANNOT_BE_EMPTY));
        errors.add(new ValidationError("field", ErrorCode.JOB_NOT_FOUND));
        errors.add(new ValidationError("field2", ErrorCode.FIELD_VALUE_TOO_BIG));
        errors.add(new ValidationError("field2", ErrorCode.UNEXPECTED_ERROR, Map.of("unexpectedDetailKey", "unexpectedDetailMessage")));
        errors.add(new ValidationError("field3", ErrorCode.USER_NOT_FOUND));

        ValidationErrors errors2  = new ValidationErrors();
        errors2.add(new ValidationError("field", ErrorCode.USER_NOT_FOUND));
        // this one is a duplicate and should not be added
        errors2.add(new ValidationError("field", ErrorCode.JOB_NOT_FOUND));
        // this one is a duplicate and should replace the other one because it has details
        errors2.add(new ValidationError("field2", ErrorCode.FIELD_VALUE_TOO_BIG, Map.of("valueTooBigDetailKey", "valueTooBigDetailMessage")));
        errors2.add(new ValidationError("field2", ErrorCode.FIELD_VALUE_TOO_BIG, Map.of("secondValueTooBigDetailKey", "secondValueTooBigDetailMessage")));

        errors2.add(new ValidationError("field4", ErrorCode.UNEXPECTED_ERROR));

        // when
        errors.merge(errors2);

        // then
        assertEquals(4, errors.getErrors().size());
        var fieldErrors = errors.getErrors().get("field");
        assertEquals(3, fieldErrors.size());
        // errors should have been merged, keeping the first ValidationErrors order
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, fieldErrors.getFirst().code());
        assertEquals(ErrorCode.JOB_NOT_FOUND, fieldErrors.get(1).code());
        assertEquals(ErrorCode.USER_NOT_FOUND, fieldErrors.get(2).code());

        var field2Errors = errors.getErrors().get("field2");
        assertEquals(2, field2Errors.size());

        assertEquals(ErrorCode.FIELD_VALUE_TOO_BIG, field2Errors.getFirst().code());
        // details of the second ValidationErrors should have been added
        assertNotNull(field2Errors.getFirst().details());

        var valueTooBigDetails = field2Errors.getFirst().details();
        assertEquals(2, valueTooBigDetails.size());
        assertEquals("valueTooBigDetailMessage", valueTooBigDetails.get("valueTooBigDetailKey"));
        assertEquals("secondValueTooBigDetailMessage", valueTooBigDetails.get("secondValueTooBigDetailKey"));

        // details of the UNEXPECTED_ERROR ValidationErrors should have been added
        assertNotNull(field2Errors.get(1).details());
        assertEquals(ErrorCode.UNEXPECTED_ERROR, field2Errors.get(1).code());
        assertEquals("unexpectedDetailMessage", field2Errors.get(1).details().get("unexpectedDetailKey"));


        assertEquals(ErrorCode.USER_NOT_FOUND, errors.getErrors().get("field3").getFirst().code());

        assertEquals(1, errors.getErrors().get("field4").size());
    }
}