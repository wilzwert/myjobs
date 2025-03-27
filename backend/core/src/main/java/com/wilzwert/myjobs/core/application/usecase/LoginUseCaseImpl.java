package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.ports.driven.Authenticator;
import com.wilzwert.myjobs.core.domain.ports.driving.LoginUseCase;
import com.wilzwert.myjobs.core.domain.services.UserDomainService;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:10
 */

public class LoginUseCaseImpl implements LoginUseCase {

    private final UserDomainService userDomainService;
    private final Authenticator authenticator;

    public LoginUseCaseImpl(UserDomainService userDomainService, Authenticator authenticator) {
        this.userDomainService = userDomainService;
        this.authenticator = authenticator;
    }

    @Override
    public AuthenticatedUser authenticateUser(String email, String password) {
        return authenticator.authenticate(userDomainService.authenticateUser(email, password));
    }
}
