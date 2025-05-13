package com.wilzwert.myjobs.infrastructure.security.service;


import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
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
 * Date:13/03/2025
 * Time:15:58
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException, NumberFormatException {
        // we use the id as jwt token subject, therefore we load the user by its id
        User foundUser = userService.findById(new UserId(UUID.fromString(id))).orElseThrow(() -> new UsernameNotFoundException("Cannot load user info"));
        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getUsername(), foundUser.getRole(), foundUser.getPassword(), Collections.of(new SimpleGrantedAuthority(foundUser.getRole())));
    }
}