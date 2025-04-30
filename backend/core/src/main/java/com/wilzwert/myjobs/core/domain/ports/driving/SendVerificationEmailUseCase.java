package com.wilzwert.myjobs.core.domain.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.UserId;

public interface SendVerificationEmailUseCase {
    void sendVerificationEmail(UserId userId);
}
