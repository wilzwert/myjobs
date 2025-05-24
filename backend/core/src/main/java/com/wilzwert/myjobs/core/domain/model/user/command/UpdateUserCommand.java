package com.wilzwert.myjobs.core.domain.model.user.command;


import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */

public record UpdateUserCommand(String email, String username, String firstName, String lastName, Integer jobFollowUpReminderDays, UserId userId) {
}
