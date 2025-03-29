package com.wilzwert.myjobs.core.domain.ports.driven;

import com.wilzwert.myjobs.core.domain.model.User;

public interface PasswordResetMessageProvider {
    void send(User user);
}
