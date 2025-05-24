package com.wilzwert.myjobs.core.domain.shared.validation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 * Date:17/05/2025
 * Time:18:00
 */

public class ValidatorTest {

    @Test
    void whenEmpty_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireNotEmpty("field", "");
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_CANNOT_BE_EMPTY, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenNotEmpty_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireNotEmpty("field", "not empty");
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenInvalidEmail_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireValidEmail("field", "invalid");
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.INVALID_EMAIL, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenValidEmail_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireValidEmail("field", "email@example.com");
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenInvalidUrl_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireValidUrl("field", "http://invalid");
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.INVALID_URL, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenValidUrl_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireValidUrl("field", "https://example.com");
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMinLengthLessThan1_thenShouldThrowAssertionError() {
        Validator validator = new Validator();
        assertThrows(AssertionError.class, () -> validator.requireMinLength("field", "value", 0));
    }

    @Test
    void whenMinLengthNotMet_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMinLength("field", "value", 6);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_TOO_SHORT, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenMinLengthMet_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMinLength("field", "value", 4);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMaxLengthLessThan1_thenShouldThrowAssertionError() {
        Validator validator = new Validator();
        assertThrows(AssertionError.class, () -> validator.requireMaxLength("field", "value", 0));
    }

    @Test
    void whenMaxLengthNotMet_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMaxLength("field", "value", 2);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_TOO_LONG, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenMaxLengthMet_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMaxLength("field", "value", 6);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMaxNotMet_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMax("field", 3, 2);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_BIG, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenMaxMet_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMax("field", 3, 3);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMinNotMet_thenShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMin("field", 2, 3);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_SMALL, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenMinMet_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMin("field", 7, 6);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenValueIsNull_thenMinIfNotNullShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMinIfNotNull("field", null, 2);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMinMet_thenMinIfNotNullShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMinIfNotNull("field", 6, 6);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMinNotMet_thenMinIfNotNullShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMinIfNotNull("field", 6, 7);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_SMALL, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenValueIsNull_thenMaxIfNotNullShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMaxIfNotNull("field", null, 2);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMaxMet_thenMaxIfNotNullShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.requireMaxIfNotNull("field", 6, 6);
        assertEquals(0, validator.getErrors().getErrors().size());
    }

    @Test
    void whenMaxNotMet_thenMaxIfNotNullShouldAddValidationError() {
        Validator validator = new Validator();
        validator.requireMaxIfNotNull("field", 7, 6);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.FIELD_VALUE_TOO_BIG, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenRequireSupplierIsFalse_thenShouldAddValidationErrorWithProvidedErrorCode() {
        Validator validator = new Validator();
        validator.require("field", () -> false, ErrorCode.UNEXPECTED_ERROR);
        assertEquals(1, validator.getErrors().getErrors().size());
        assertEquals(ErrorCode.UNEXPECTED_ERROR, validator.getErrors().getErrors().get("field").getFirst().code());
    }

    @Test
    void whenRequireSupplierIsTrue_thenShouldNotAddValidationError() {
        Validator validator = new Validator();
        validator.require("field", () -> true, ErrorCode.UNEXPECTED_ERROR);
        assertEquals(0, validator.getErrors().getErrors().size());
    }
}
