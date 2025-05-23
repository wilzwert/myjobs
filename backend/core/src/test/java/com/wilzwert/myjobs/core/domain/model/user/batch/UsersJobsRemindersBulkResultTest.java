package com.wilzwert.myjobs.core.domain.model.user.batch;


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

public class UsersJobsRemindersBulkResultTest {

    @Test
    public void whenSendErrorsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsRemindersBulkResult(0, 0, Collections.emptyList(), -1, 0));
        assertEquals("sendErrorsCount count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    public void whenSaveErrorsCountLessThanZero_thenShouldThrowIllegalArgumentException() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new UsersJobsRemindersBulkResult(0, 0, Collections.emptyList(), 0, -1));
        assertEquals("saveErrorsCount count must be greater than or equal to 0", ex.getMessage());
    }

    @Test
    public void shouldCreateUsersJobsRemindersBulkResult() {
        var result = new UsersJobsRemindersBulkResult(10, 10, List.of("test error"), 1, 2);
        assertEquals(2, result.getSaveErrorsCount());
        assertEquals(1, result.getSendErrorsCount());
        assertEquals(1, result.getErrors().size());
        assertEquals("test error", result.getErrors().getFirst());
    }

    @Test
    public void shouldGetToString() {
        var result = new UsersJobsRemindersBulkResult(10, 10, List.of("test error"), 1, 2);
        assertEquals("UsersJobsRemindersBulkResult [sendErrorsCount=1, saveErrorsCount=2, usersCount=10, jobsCount=10]", result.toString());
    }
}