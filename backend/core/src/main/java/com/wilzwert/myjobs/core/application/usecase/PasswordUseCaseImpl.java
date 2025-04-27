package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.exception.PasswordMatchException;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.CreateNewPasswordUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.ResetPasswordUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.ChangePasswordUseCase;

public class PasswordUseCaseImpl implements ResetPasswordUseCase, CreateNewPasswordUseCase, ChangePasswordUseCase {

    private final UserService userService;

    private final PasswordResetMessageProvider messageProvider;

    private final PasswordHasher passwordHasher;

    public PasswordUseCaseImpl(UserService userService, PasswordResetMessageProvider messageProvider, PasswordHasher passwordHasher) {
        this.userService = userService;
        this.messageProvider = messageProvider;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void createNewPassword(CreatePasswordCommand createPasswordCommand) {
        // find user by token
        // if not found, do nothing (business rule is to not send a "not found error")
        userService.findByResetPasswordToken(createPasswordCommand.resetPasswordToken()).ifPresent((user) -> {
            // store new password
            userService.save(user.createNewPassword(createPasswordCommand.password(), passwordHasher.hashPassword(createPasswordCommand.password())));
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

    @Override
    public void changePassword(ChangePasswordCommand changePasswordCommand) {
        User user = userService.findById(changePasswordCommand.userId()).orElseThrow(UserNotFoundException::new);

        if(!passwordHasher.verifyPassword(changePasswordCommand.oldPassword(), user.getPassword())) {
            throw new PasswordMatchException();
        }

        user = user.updatePassword(changePasswordCommand.password(), passwordHasher.hashPassword(changePasswordCommand.password()));
        userService.save(user);
    }
}
