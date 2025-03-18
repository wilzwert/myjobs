package com.wilzwert.myjobs.core.domain.ports.driven;


import com.wilzwert.myjobs.core.domain.model.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.User;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:53
 */
public interface Authenticator {
    AuthenticatedUser authenticate(User user);
}
