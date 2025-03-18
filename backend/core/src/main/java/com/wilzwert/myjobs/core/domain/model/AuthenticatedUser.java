package com.wilzwert.myjobs.core.domain.model;


/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:19:41
 */
public interface AuthenticatedUser {
    String getEmail();
    String getUsername();
    String getRole();

}
