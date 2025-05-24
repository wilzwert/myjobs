package com.wilzwert.myjobs.infrastructure.security.service;


import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Custom UserDetailsService used to retrieve user by username extracted from a jwt token subject
 * As of now, the jwt token subject is the user's email
 *
 * @author Wilhelm Zwertvaegher
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDataManager userDataManager;

    public CustomUserDetailsService(final UserDataManager userDataManager) {
        this.userDataManager = userDataManager;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException, NumberFormatException {
        // we use the id as jwt token subject, therefore we load the user by its id
        User foundUser = userDataManager.findById(new UserId(UUID.fromString(id))).orElseThrow(() -> new UsernameNotFoundException("Cannot load user info"));
        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getUsername(), foundUser.getRole(), foundUser.getPassword(), Collections.of(new SimpleGrantedAuthority(foundUser.getRole())));
    }
}