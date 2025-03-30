package com.wilzwert.myjobs.core.domain.ports.driven;

import com.wilzwert.myjobs.core.domain.model.User;

public interface AccountCreationMessageProvider {
    void send(User user);
}
