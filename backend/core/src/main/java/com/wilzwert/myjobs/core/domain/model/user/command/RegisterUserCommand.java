package com.wilzwert.myjobs.core.domain.model.user.command;


import com.wilzwert.myjobs.core.domain.model.user.Lang;

/**
 * @author Wilhelm Zwertvaegher
 */

public record RegisterUserCommand(String email, String password, String username, String firstName, String lastName, Lang lang, Integer jobFollowUpReminderDays) {
    public RegisterUserCommand(String email, String password, String username, String firstName, String lastName, Lang lang) {
        this(email, password, username, firstName, lastName, lang, null);
    }
}
