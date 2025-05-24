package com.wilzwert.myjobs.core.domain.model.user.batch;


import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Wilhelm Zwertvaegher
 */

class UsersJobsRemindersBulkResultTest {

    @Test
    void whenSendErrorsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var errors = Collections.<String>emptyList();
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsRemindersBulkResult(0, 0, errors, -1, 0));
        assertEquals("sendErrorsCount count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    void whenSaveErrorsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var errors = Collections.<String>emptyList();
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsRemindersBulkResult(0, 0, errors, 0, -1));
        assertEquals("saveErrorsCount count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    void shouldCreateUsersJobsRemindersBulkResult() {
        var result = new UsersJobsRemindersBulkResult(10, 10, List.of("test error"), 1, 2);
        assertEquals(2, result.getSaveErrorsCount());
        assertEquals(1, result.getSendErrorsCount());
        assertEquals(1, result.getErrors().size());
        assertEquals("test error", result.getErrors().getFirst());
    }

    @Test
    void shouldGetToString() {
        var result = new UsersJobsRemindersBulkResult(10, 10, List.of("test error"), 1, 2);
        assertEquals("UsersJobsRemindersBulkResult [sendErrorsCount=1, saveErrorsCount=2, usersCount=10, jobsCount=10]", result.toString());
    }
}