package com.wilzwert.myjobs.core.domain.shared.bulk;


import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 */

class UsersJobsBulkResultTest {

    @Test
    void whenUsersCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var errors = Collections.<String>emptyList();
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(-1, 0, errors));
        assertEquals("users count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    void whenJobsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var errors = Collections.<String>emptyList();
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, -1, errors));
        assertEquals("jobs count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    void whenErrorsListEmpty_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, 0, null));
        assertEquals("errors must not be null", ex.getMessage());
    }

    @Test
    void shouldCreateUsersJobsBulkResult() {
        var result = new UsersJobsBulkResult(10, 10, List.of("test error"));
        assertEquals(10, result.getUsersCount());
        assertEquals(10, result.getJobsCount());
        assertEquals(1, result.getErrors().size());
        assertEquals("test error", result.getErrors().getFirst());
    }
}