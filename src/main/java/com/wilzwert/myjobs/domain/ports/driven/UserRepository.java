package com.wilzwert.myjobs.domain.ports.driven;


import com.wilzwert.myjobs.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findById(UUID id);

    Optional<User> findByIdWithJobs(UUID id);

    User save(User user);
}
