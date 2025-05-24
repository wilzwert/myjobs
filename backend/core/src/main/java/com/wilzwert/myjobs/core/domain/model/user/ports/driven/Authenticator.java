package com.wilzwert.myjobs.core.domain.model.user.ports.driven;


import com.wilzwert.myjobs.core.domain.model.user.AuthenticatedUser;
import com.wilzwert.myjobs.core.domain.model.user.User;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface Authenticator {
    AuthenticatedUser authenticate(User user);
}
