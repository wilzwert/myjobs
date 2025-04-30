package com.wilzwert.myjobs.core.domain.command;


import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record DeleteUserCommand(UserId userId) {
}



