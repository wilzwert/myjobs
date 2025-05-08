package com.wilzwert.myjobs.core.domain.command;


import com.wilzwert.myjobs.core.domain.model.user.Lang;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record RegisterUserCommand(String email, String password, String username, String firstName, String lastName, Lang lang) {
}
