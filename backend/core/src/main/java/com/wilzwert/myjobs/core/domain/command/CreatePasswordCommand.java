package com.wilzwert.myjobs.core.domain.command;

public record CreatePasswordCommand(String password, String resetPasswordToken) {}
