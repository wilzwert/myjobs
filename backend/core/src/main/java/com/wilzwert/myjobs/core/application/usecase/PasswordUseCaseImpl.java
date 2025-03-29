package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.command.PasswordCommand;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CreateNewPasswordUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.ResetPasswordUseCase;

public class PasswordUseCaseImpl implements ResetPasswordUseCase, CreateNewPasswordUseCase {

    private final UserService userService;

    private final PasswordResetMessageProvider messageProvider;

    private final PasswordHasher passwordHasher;

    public PasswordUseCaseImpl(UserService userService, PasswordResetMessageProvider messageProvider, PasswordHasher passwordHasher) {
        this.userService = userService;
        this.messageProvider = messageProvider;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void createNewPassword(PasswordCommand passwordCommand) {
        // find user by token
        // if not found, do nothing (business rule is to not send a "not found error")
        userService.findByResetPasswordToken(passwordCommand.resetPasswordToken()).ifPresent((user) -> {
            // store new password
            userService.save(user.createNewPassword(passwordHasher.hashPassword(passwordCommand.password())));
        });
    }

    @Override
    public void resetPassword(String email) {
        // find user
        // if not found, do nothing (business rule is to not send a "not found error")
        userService.findByEmail(email).ifPresent((user) -> {
            User updatedUser = user.resetPassword();

            messageProvider.send(updatedUser);

            userService.save(updatedUser);
        });
    }
}
