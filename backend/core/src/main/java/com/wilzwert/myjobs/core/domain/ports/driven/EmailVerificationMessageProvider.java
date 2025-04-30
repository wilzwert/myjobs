package com.wilzwert.myjobs.core.domain.ports.driven;

import com.wilzwert.myjobs.core.domain.model.user.User;

public interface EmailVerificationMessageProvider {
    void send(User user);
}
