package com.wilzwert.myjobs.core.domain.model.user.ports.driven;

import com.wilzwert.myjobs.core.domain.model.user.User;

public interface PasswordResetMessageProvider {
    void send(User user);
}
