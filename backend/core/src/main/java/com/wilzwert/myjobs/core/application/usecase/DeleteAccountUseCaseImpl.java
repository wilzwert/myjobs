package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:31/03/2025
 * Time:10:43
 */

public class DeleteAccountUseCaseImpl implements DeleteAccountUseCase {
    private final UserService userService;

    public DeleteAccountUseCaseImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void deleteAccount(UserId userId) {
        User user = userService.findById(userId).orElseThrow(UserNotFoundException::new);
        userService.deleteUser(user);
    }
}
