package com.wilzwert.myjobs.core.domain.model.user.ports.driving;

import com.wilzwert.myjobs.core.domain.model.user.UserId;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface DeleteAccountUseCase {
    void deleteAccount(UserId userId);
}
