package com.wilzwert.myjobs.core.domain.model.user.ports.driving;


/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:09:20
 */

public interface CheckUserAvailabilityUseCase {
    boolean isEmailTaken(String email);
    boolean isUsernameTaken(String username);
}
