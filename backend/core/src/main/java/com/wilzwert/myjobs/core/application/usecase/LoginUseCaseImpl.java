package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:10
 */

public class LoginUseCaseImpl implements LoginUseCase {

    private final UserService userService;
    private final PasswordHasher passwordHasher;
    private final Authenticator authenticator;

    public LoginUseCaseImpl(UserService userService, PasswordHasher passwordHasher, Authenticator authenticator) {
        this.userService = userService;
        this.passwordHasher = passwordHasher;
        this.authenticator = authenticator;
    }


    @Override
    public AuthenticatedUser authenticateUser(String email, String password) {
        User user = userService.findByEmail(email)
                .orElseThrow(LoginException::new);

        if(!passwordHasher.verifyPassword(password, user.getPassword())) {
            throw new LoginException();
        }

        return authenticator.authenticate(user);
    }
}
