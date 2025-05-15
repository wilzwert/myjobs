package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserService;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkServiceSaveResult;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoJob;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoRefreshTokenRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoUserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:10
 */
@Component
public class UserServiceAdapter implements UserService {
    private final MongoUserRepository mongoUserRepository;
    private final MongoJobRepository mongoJobRepository;
    private final AggregationService aggregationService;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;

    public UserServiceAdapter(final MongoUserRepository mongoUserRepository, final MongoJobRepository mongoJobRepository, final AggregationService aggregationService, final UserMapper userMapper, JobMapper jobMapper, MongoRefreshTokenRepository mongoRefreshTokenRepository) {
        this.mongoUserRepository = mongoUserRepository;
        this.mongoJobRepository = mongoJobRepository;
        this.aggregationService = aggregationService;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
    }

    @Override
    public List<UserView> findView(DomainSpecification<User> specifications) {
        Aggregation aggregation = aggregationService.createAggregation(specifications, "id");
        return this.userMapper.toDomainView(aggregationService.aggregate(aggregation, "users", MongoUser.class));
    }

    @Override
    public Map<UserId, User> findMinimal(DomainSpecification<User> specifications) {
        Aggregation aggregation = aggregationService.createAggregation(specifications, "id");
        return this.userMapper.toDomain(aggregationService.aggregate(aggregation, "users", MongoUser.class))
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public Optional<UserView> findViewByEmail(String email) {
        return mongoUserRepository.findByEmail(email).map(userMapper::toDomainView).or(Optional::empty);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return getFullUser(mongoUserRepository.findByEmail(email));
    }

    @Override
    public Optional<User> findByEmailValidationCode(String code) {
        return getFullUser(mongoUserRepository.findByEmailValidationCode(code));
    }

    @Override
    public Optional<User> findByResetPasswordToken(String token) {
        return getFullUser(mongoUserRepository.findByResetPasswordToken(token));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return getFullUser(mongoUserRepository.findByUsername(username));
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        return getFullUser(mongoUserRepository.findByEmailOrUsername(email, username));
    }

    @Override
    public Optional<UserView> findViewById(UserId id) {
        return mongoUserRepository.findById(id.value()).map(userMapper::toDomainView).or(Optional::empty);
    }

    private Optional<User> getFullUser(Optional<MongoUser> user) {
        List<MongoJob> mongoJobs = mongoJobRepository.findByUserId(user.get().getId());
        return user.map(u -> userMapper.toDomain(u).completeWith(jobMapper.toDomain(mongoJobs)));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return getFullUser(mongoUserRepository.findById(id.value()));
    }

    @Override
    public Optional<User> findByIdMinimal(UserId id) {
        return (mongoUserRepository.findById(id.value()).map(userMapper::toDomain));
    }


    /**
     * Saves a User, and updates their username and email in the lookup cache
     * @param user the User to save
     * @return the saved User
     */
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "emailExists", key = "#user.email"),
            @CacheEvict(value = "usernameExists", key = "#user.username")
        }
    )
    public User save(User user) {
        return this.userMapper.toDomain(mongoUserRepository.save(this.userMapper.toEntity(user)));
    }


    @Override
    @Transactional
    public User saveUserAndJob(User user, Job job) {
        this.mongoJobRepository.save(this.jobMapper.toEntity(job));
        return this.save(user);
    }

    @Override
    @Transactional
    public User deleteJobAndSaveUser(User user, Job job) {
        this.mongoJobRepository.delete(this.jobMapper.toEntity(job));
        return this.userMapper.toDomain(this.mongoUserRepository.save(this.userMapper.toEntity(user)));
    }

    @Override
    @Cacheable(value = "emailExists", key = "#email")
    public boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }

    @Override
    @Cacheable(value = "usernameExists", key = "#username")
    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }


    /**
     * Deletes a user, and deletes their username and email from the lookup cache
     * @param user the User to delete
     */
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "emailExists", key = "#user.email"),
            @CacheEvict(value = "usernameExists", key = "#user.username")

        }
    )
    @Transactional
    public void deleteUser(User user) {
        mongoJobRepository.deleteByUserId(user.getId().value());
        mongoRefreshTokenRepository.deleteByUserId(user.getId().value());
        mongoUserRepository.delete(userMapper.toEntity(user));
    }

    @Override
    public BulkServiceSaveResult saveAll(Set<User> users) {

        return new BulkServiceSaveResult(0, 0, 0);
    }
}