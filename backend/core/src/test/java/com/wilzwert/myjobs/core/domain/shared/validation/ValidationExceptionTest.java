package com.wilzwert.myjobs.core.domain.shared.validation;


import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:19/05/2025
 * Time:09:53
 */

public class ValidationExceptionTest {
    @Test
    void shouldReturnFlatErrorsAsAList() {
        // create some validation errors
        Validator validator = new Validator();
        validator.requireValidUrl("url", "http://example");
        // this makes no sense, we only want to check the behaviour when one field has several errors
        validator.requireMin("url", 1, 2);
        validator.requireMin("age", 16, 18);

        if(!validator.getErrors().hasErrors()) {
            fail("Validation should have failed");
        }

        ValidationException exception = new ValidationException(validator.getErrors());

        List<ValidationError> errors = exception.getFlatErrors();
        assertEquals(3, errors.size());
        assertEquals(ErrorCode.INVALID_URL, errors.getFirst().code());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_SMALL, errors.get(1).code());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_SMALL, errors.get(2).code());
    }
}
