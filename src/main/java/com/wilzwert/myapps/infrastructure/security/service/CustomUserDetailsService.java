package com.wilzwert.myapps.infrastructure.security.service;


import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.domain.ports.driven.UserRepository;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;

    public CustomUserDetailsService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, NumberFormatException {
        // we use the id as jwt token subject, therefore we load the user by its id
        User foundUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return new UserDetailsImpl(foundUser.getId(), foundUser.getEmail(), foundUser.getUsername(), foundUser.getRole(), foundUser.getPassword(), Collections.of(new SimpleGrantedAuthority(foundUser.getRole())));
    }
}