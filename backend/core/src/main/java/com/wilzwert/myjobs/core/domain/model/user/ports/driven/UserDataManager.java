package com.wilzwert.myjobs.core.domain.model.user.ports.driven;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 * Method findMinimal* allow the infra to define ways of loading a user without its related entities / aggregates
 * It is useful when we need to perform an action only the user (i.e. with no side effects on related aggregates)
 * In some cases, it may improve performance as Infra will be able e.g. in a relational DB to load the user with no joins on jobs,
 * In other cases, (e.g. nested collections in nosql) infra will handle it differently
 * Warning ! these methodes are to be used with caution because in some cases it may lead to exceptions or aggregates inconsistency
 * User aggregate consistency is ensured by throwing exceptions in cases actions require the full aggregate to be loaded
 * Only use cases should dictate the loading strategy minimal / full because they are the actions orchestrators
 */
public interface UserDataManager {

    List<UserView> findView(DomainSpecification specifications);

    Map<UserId, User> findMinimal(DomainSpecification specifications);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetPasswordToken(String code);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);

    Optional<UserView> findViewById(UserId id);

    Optional<User> findById(UserId id);

    Optional<User> findMinimalByUsername(String username);

    Optional<User> findMinimalByEmail(String email);

    Optional<User> findMinimalByEmailValidationCode(String code);

    Optional<User> findMinimalById(UserId id);

    User save(User user);

    User saveUserAndJob(User user, Job job);

    User deleteJobAndSaveUser(User user, Job job);

    boolean emailExists(String email);

    boolean usernameExists(String username);

    /**
     * Deletes the User
     * Important : all related entities MUST be deleted too
     * @param user the User to delete
     */
    void deleteUser(User user);

    BulkDataSaveResult saveAll(Set<User> users);
}
