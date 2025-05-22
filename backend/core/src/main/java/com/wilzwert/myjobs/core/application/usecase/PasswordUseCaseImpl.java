package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.user.command.CreatePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.ChangePasswordCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.PasswordMatchException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordResetMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.CreateNewPasswordUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ResetPasswordUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.ChangePasswordUseCase;

public class PasswordUseCaseImpl implements ResetPasswordUseCase, CreateNewPasswordUseCase, ChangePasswordUseCase {

    private final UserDataManager userDataManager;

    private final PasswordResetMessageProvider messageProvider;

    private final PasswordHasher passwordHasher;

    public PasswordUseCaseImpl(UserDataManager userDataManager, PasswordResetMessageProvider messageProvider, PasswordHasher passwordHasher) {
        this.userDataManager = userDataManager;
        this.messageProvider = messageProvider;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void createNewPassword(CreatePasswordCommand createPasswordCommand) {
        // find user by token
        // if not found, do nothing (business rule is to not send a "not found error")
        userDataManager.findByResetPasswordToken(createPasswordCommand.resetPasswordToken()).ifPresent((user) -> {
            // store new password
            userDataManager.save(user.createNewPassword(createPasswordCommand.password(), passwordHasher.hashPassword(createPasswordCommand.password())));
        });
    }

    @Override
    public void resetPassword(String email) {
        // find user
        // if not found, do nothing (business rule is to not send a "not found error")
        userDataManager.findByEmail(email).ifPresent((user) -> {
            User updatedUser = user.resetPassword();

            messageProvider.send(updatedUser);

            userDataManager.save(updatedUser);
        });
    }

    @Override
    public void changePassword(ChangePasswordCommand changePasswordCommand) {
        User user = userDataManager.findById(changePasswordCommand.userId()).orElseThrow(UserNotFoundException::new);

        if(!passwordHasher.verifyPassword(changePasswordCommand.oldPassword(), user.getPassword())) {
            throw new PasswordMatchException();
        }

        user = user.updatePassword(changePasswordCommand.password(), passwordHasher.hashPassword(changePasswordCommand.password()));
        userDataManager.save(user);
    }
}
