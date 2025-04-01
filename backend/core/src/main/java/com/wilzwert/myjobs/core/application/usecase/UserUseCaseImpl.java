package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.ports.driving.SendVerificationEmailUseCase;
import com.wilzwert.myjobs.core.domain.ports.driving.UpdateUserUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/03/2025
 * Time:16:55
 */

public class UserUseCaseImpl implements SendVerificationEmailUseCase, UpdateUserUseCase {

    private UserService userService;

    private EmailVerificationMessageProvider emailVerificationMessageProvider;

    public UserUseCaseImpl(UserService userService, EmailVerificationMessageProvider emailVerificationMessageProvider) {
        this.userService = userService;
        this.emailVerificationMessageProvider = emailVerificationMessageProvider;
    }

    @Override
    public void sendVerificationEmail(UserId userId) {
        userService.findById(userId).ifPresent(user -> {
            emailVerificationMessageProvider.send(user);
        });
    }

    @Override
    public User updateUser(UserId userId, UpdateUserCommand command) {
        User user = userService.findById(userId).orElseThrow(UserNotFoundException::new);

        boolean shouldResendVerificationEmail = !user.getEmail().equals(command.email());

        user = userService.save(user.update(command.email(), command.username(), command.firstName(), command.lastName()));

        if(shouldResendVerificationEmail) {
            emailVerificationMessageProvider.send(user);
        }
        return user;
    }
}
