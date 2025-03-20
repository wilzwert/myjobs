package com.wilzwert.myjobs.core.domain.ports.driving;


/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:09:20
 */

public interface CheckUserAvailabilityUseCase {
    boolean isEmailTaken(String email);
    boolean isUsernameTaken(String username);
}
