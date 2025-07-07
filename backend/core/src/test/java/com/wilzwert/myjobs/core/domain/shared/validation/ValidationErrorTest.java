package com.wilzwert.myjobs.core.domain.shared.validation;


import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 */

class ValidationErrorTest {
    @Test
    void shouldGenerateToString() {
        var details = Map.of("detailLabel", "detailDescription");
        ValidationError error = new ValidationError("name", ErrorCode.UNEXPECTED_ERROR, details);

        assertNotNull(error);
        assertEquals("name", error.field());
        assertEquals(ErrorCode.UNEXPECTED_ERROR, error.code());
        assertEquals(details, error.details());
        assertEquals("ValidationError[code = "+ErrorCode.UNEXPECTED_ERROR+", field = name, details = "+details+"]", error.toString());
    }
}