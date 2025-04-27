package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoRefreshTokenRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoUserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:10
 */
@Component
public class UserServiceAdapter implements UserService {
    private final MongoUserRepository mongoUserRepository;
    private final MongoJobRepository mongoJobRepository;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;

    public UserServiceAdapter(final MongoUserRepository mongoUserRepository, final MongoJobRepository mongoJobRepository, final UserMapper userMapper, JobMapper jobMapper, MongoRefreshTokenRepository mongoRefreshTokenRepository) {
        this.mongoUserRepository = mongoUserRepository;
        this.mongoJobRepository = mongoJobRepository;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
    }

    @Override
    public boolean isEmailAndUsernameAvailable(String email, String username) {
        return findByEmailOrUsername(email, username).isEmpty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByEmailValidationCode(String code) {
        return mongoUserRepository.findByEmailValidationCode(code).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByResetPasswordToken(String code) {
        return mongoUserRepository.findByResetPasswordToken(code).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        System.out.println(mongoUserRepository.findByEmailOrUsername(email, username));
        return mongoUserRepository.findByEmailOrUsername(email, username).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return mongoUserRepository.findById(id.value()).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByIdWithJobs(UserId id) {
        return findById(id).map(u -> u.withJobs(jobMapper.toDomain(mongoJobRepository.findByUserId(u.getId().value(), null).getContent())));
    }

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
        System.out.println("in emailExists "+email);
        return findByEmail(email).isPresent();
    }

    @Override
    @Cacheable(value = "usernameExists", key = "#username")
    public boolean usernameExists(String username) {
        System.out.println("in usernameExists "+username);
        return findByUsername(username).isPresent();
    }

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
}