package com.wilzwert.myjobs.core.domain.model.user.ports.driven;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.util.List;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface UserService {

    List<UserView> findView(DomainSpecification<User> specifications);

    Optional<User> findByEmail(String email);

    Optional<UserView> findViewByEmail(String email);

    Optional<User> findByEmailValidationCode(String code);

    Optional<User> findByResetPasswordToken(String code);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);

    Optional<UserView> findViewById(UserId id);

    Optional<User> findById(UserId id);

    User save(User user);

    User saveUserAndJob(User user, Job job);

    User deleteJobAndSaveUser(User user, Job job);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    void deleteUser(User user);
}
