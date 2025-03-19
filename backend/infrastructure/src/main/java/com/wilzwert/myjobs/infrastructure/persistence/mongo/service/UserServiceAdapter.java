package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.model.Job;
import com.wilzwert.myjobs.core.domain.model.User;
import com.wilzwert.myjobs.core.domain.model.UserId;
import com.wilzwert.myjobs.core.domain.ports.driven.UserService;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoJobRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.repository.MongoUserRepository;
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

    public UserServiceAdapter(final MongoUserRepository mongoUserRepository, final MongoJobRepository mongoJobRepository, final UserMapper userMapper, JobMapper jobMapper) {
        this.mongoUserRepository = mongoUserRepository;
        this.mongoJobRepository = mongoJobRepository;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoUserRepository.findByEmail(email).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        return mongoUserRepository.findByEmailOrUsername(email, username).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return mongoUserRepository.findById(id.value()).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByIdWithJobs(UserId id) {
        return findById(id).map(u -> u.withJobs(jobMapper.toDomain(mongoJobRepository.findByUserId(u.getId().value(), null))));
    }

    @Override
    public User save(User user) {
        return this.userMapper.toDomain(mongoUserRepository.save(this.userMapper.toEntity(user)));
    }


    @Override
    @Transactional
    public User saveUserAndJob(User user, Job job) {
        this.mongoJobRepository.save(this.jobMapper.toEntity(job));
        return this.userMapper.toDomain(this.mongoUserRepository.save(this.userMapper.toEntity(user)));
    }

    @Override
    public User deleteJobAndSaveUser(User user, Job job) {
        this.mongoJobRepository.delete(this.jobMapper.toEntity(job));
        return this.userMapper.toDomain(this.mongoUserRepository.save(this.userMapper.toEntity(user)));
    }
}
