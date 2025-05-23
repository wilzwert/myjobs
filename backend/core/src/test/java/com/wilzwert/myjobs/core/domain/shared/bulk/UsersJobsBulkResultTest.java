package com.wilzwert.myjobs.core.domain.shared.bulk;


import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 * Date:23/05/2025
 * Time:16:27
 */

public class UsersJobsBulkResultTest {

    @Test
    public void whenUsersCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(-1, 0, Collections.emptyList()));
        assertEquals("users count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    public void whenJobsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, -1, Collections.emptyList()));
        assertEquals("jobs count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    public void whenErrorsListEmpty_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, 0, null));
        assertEquals("errors must not be null", ex.getMessage());
    }

    @Test
    public void shouldCreateUsersJobsBulkResult() {
        var result = new UsersJobsBulkResult(10, 10, List.of("test error"));
        assertEquals(10, result.getUsersCount());
        assertEquals(10, result.getJobsCount());
        assertEquals(1, result.getErrors().size());
        assertEquals("test error", result.getErrors().getFirst());
    }
}