package com.wilzwert.myjobs.core.domain.model.user.command;

import com.wilzwert.myjobs.core.domain.model.user.UserId;

public record ChangePasswordCommand(String password, String oldPassword, UserId userId) {}
