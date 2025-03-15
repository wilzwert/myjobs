package com.wilzwert.myjobs.domain.ports.driving;


import com.wilzwert.myjobs.domain.model.AuthenticatedUser;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:11
 */

public interface LoginUseCase {
    AuthenticatedUser authenticateUser(String username, String password);
}
