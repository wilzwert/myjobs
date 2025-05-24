package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


/**
 * @author Wilhelm Zwertvaegher
 */

public interface CheckUserAvailabilityUseCase {
    boolean isEmailTaken(String email);
    boolean isUsernameTaken(String username);
}
