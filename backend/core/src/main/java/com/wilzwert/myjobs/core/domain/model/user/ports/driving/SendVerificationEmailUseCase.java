package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.UserId;

public interface SendVerificationEmailUseCase {
    void sendVerificationEmail(UserId userId);
}
