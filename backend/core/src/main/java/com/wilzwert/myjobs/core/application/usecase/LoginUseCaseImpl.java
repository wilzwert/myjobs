package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.PasswordHasher;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.LoginUseCase;

/**
 * @author Wilhelm Zwertvaegher
 */

public class LoginUseCaseImpl implements LoginUseCase {

    private final UserDataManager userDataManager;
    private final PasswordHasher passwordHasher;
    private final Authenticator authenticator;

    public LoginUseCaseImpl(UserDataManager userDataManager, PasswordHasher passwordHasher, Authenticator authenticator) {
        this.userDataManager = userDataManager;
        this.passwordHasher = passwordHasher;
        this.authenticator = authenticator;
    }


    @Override
    public AuthenticatedUser authenticateUser(String email, String password) {
        User user = userDataManager.findByEmail(email)
                .orElseThrow(LoginException::new);

        if(!passwordHasher.verifyPassword(password, user.getPassword())) {
            throw new LoginException();
        }

        return authenticator.authenticate(user);
    }
}
