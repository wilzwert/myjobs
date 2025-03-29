package com.wilzwert.myjobs.core.domain.command;

public record PasswordCommand(String password, String resetPasswordToken, String oldPassword) {}
