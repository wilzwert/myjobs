package com.wilzwert.myapps.domain.ports.driven;


import com.wilzwert.myapps.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);

    User save(User user);
}
