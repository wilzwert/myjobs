package com.wilzwert.myjobs.core.domain.model.user.command;

public record CreatePasswordCommand(String password, String resetPasswordToken) {}
