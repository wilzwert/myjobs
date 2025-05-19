package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:11
 */

public interface LoginUseCase {
    AuthenticatedUser authenticateUser(String username, String password);
}
