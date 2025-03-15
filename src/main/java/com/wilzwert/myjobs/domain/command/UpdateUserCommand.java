package com.wilzwert.myjobs.domain.command;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record UpdateUserCommand(String email, String password, String username, String firstName, String lastName) {
}
