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
        assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(-1, 0, Collections.emptyList()));
    }

    @Test
    public void whenJobsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, -1, Collections.emptyList()));
    }

    @Test
    public void whenErrorsListEmpty_thenShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new UsersJobsBulkResult(0, 0, null
        ));
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