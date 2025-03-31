package com.wilzwert.myjobs.core.domain.command;

import com.wilzwert.myjobs.core.domain.model.UserId;

public record ChangePasswordCommand(String password, String oldPassword, UserId userId) {}
