package com.wilzwert.myjobs.core.domain.model.user;


/**
 * @author Wilhelm Zwertvaegher
 */
public interface AuthenticatedUser {
    String getEmail();
    String getUsername();
    String getRole();

}
