package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;

/**
 * @author Wilhelm Zwertvaegher
 */

public interface LoginUseCase {
    AuthenticatedUser authenticateUser(String username, String password);
}
