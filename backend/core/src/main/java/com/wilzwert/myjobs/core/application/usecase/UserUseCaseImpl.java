package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendVerificationEmailUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.UpdateUserLangUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.UpdateUserUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/03/2025
 * Time:16:55
 */

public class UserUseCaseImpl implements SendVerificationEmailUseCase, UpdateUserUseCase, UpdateUserLangUseCase {

    private final UserService userService;

    private final EmailVerificationMessageProvider emailVerificationMessageProvider;

    public UserUseCaseImpl(UserService userService, EmailVerificationMessageProvider emailVerificationMessageProvider) {
        this.userService = userService;
        this.emailVerificationMessageProvider = emailVerificationMessageProvider;
    }

    @Override
    public void sendVerificationEmail(UserId userId) {
        userService.findById(userId).ifPresent(emailVerificationMessageProvider::send);
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        User user = userService.findById(command.userId()).orElseThrow(UserNotFoundException::new);

        // if email changes, check availability
        if(!command.email().equals(user.getEmail())) {
            User existingUser = userService.findByEmail(command.email()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }
        // if username changes, check availability
        if(!command.username().equals(user.getUsername())) {
            User existingUser = userService.findByUsername(command.username()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }

        boolean shouldResendVerificationEmail = !user.getEmail().equals(command.email());

        user = userService.save(user.update(command.email(), command.username(), command.firstName(), command.lastName(), command.jobFollowUpReminderDays()));

        if(shouldResendVerificationEmail) {
            emailVerificationMessageProvider.send(user);
        }
        return user;
    }

    @Override
    public User updateUserLang(UpdateUserLangCommand command) {
        User user = userService.findById(command.userId()).orElseThrow(UserNotFoundException::new);
        if(user.getLang().equals(command.lang())) {
            return user;
        }
        return userService.save(user.updateLang(command.lang()));
    }
}
