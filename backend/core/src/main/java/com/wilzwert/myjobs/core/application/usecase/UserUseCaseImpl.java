package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserCommand;
import com.wilzwert.myjobs.core.domain.model.user.command.UpdateUserLangCommand;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.EmailVerificationMessageProvider;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.GetUserViewUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.SendVerificationEmailUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.UpdateUserLangUseCase;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.UpdateUserUseCase;

/**
 * @author Wilhelm Zwertvaegher
 */

public class UserUseCaseImpl implements SendVerificationEmailUseCase, GetUserViewUseCase, UpdateUserUseCase, UpdateUserLangUseCase {

    private final UserDataManager userDataManager;

    private final EmailVerificationMessageProvider emailVerificationMessageProvider;

    public UserUseCaseImpl(UserDataManager userDataManager, EmailVerificationMessageProvider emailVerificationMessageProvider) {
        this.userDataManager = userDataManager;
        this.emailVerificationMessageProvider = emailVerificationMessageProvider;
    }

    @Override
    public void sendVerificationEmail(UserId userId) {
        userDataManager.findMinimalById(userId).ifPresent(emailVerificationMessageProvider::send);
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        User user = userDataManager.findMinimalById(command.userId()).orElseThrow(UserNotFoundException::new);

        // if email changes, check availability
        if(!command.email().equals(user.getEmail())) {
            User existingUser = userDataManager.findMinimalByEmail(command.email()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }
        // if username changes, check availability
        if(!command.username().equals(user.getUsername())) {
            User existingUser = userDataManager.findMinimalByUsername(command.username()).orElse(null);
            if(existingUser != null && !existingUser.equals(user)) {
                throw new UserAlreadyExistsException();
            }
        }

        boolean shouldResendVerificationEmail = !user.getEmail().equals(command.email());

        user = userDataManager.save(user.update(command.email(), command.username(), command.firstName(), command.lastName(), command.jobFollowUpReminderDays()));

        if(shouldResendVerificationEmail) {
            emailVerificationMessageProvider.send(user);
        }
        return user;
    }

    @Override
    public User updateUserLang(UpdateUserLangCommand command) {
        User user = userDataManager.findMinimalById(command.userId()).orElseThrow(UserNotFoundException::new);
        if(user.getLang().equals(command.lang())) {
            return user;
        }
        return userDataManager.save(user.updateLang(command.lang()));
    }

    @Override
    public UserView getUser(UserId userId) {
        return userDataManager.findViewById(userId).orElseThrow(UserNotFoundException::new);
    }
}
