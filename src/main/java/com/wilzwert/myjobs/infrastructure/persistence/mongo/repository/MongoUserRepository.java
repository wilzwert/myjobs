package com.wilzwert.myjobs.infrastructure.persistence.mongo.repository;


import com.wilzwert.myjobs.domain.model.User;
import com.wilzwert.myjobs.domain.ports.driven.UserRepository;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.JobMapper;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:16:10
 */
@Component
public class MongoUserRepository implements UserRepository {
    private final SpringMongoUserRepository springMongoUserRepository;
    private final SpringMongoJobRepository springMongoJobRepository;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;

    public MongoUserRepository(final SpringMongoUserRepository springMongoUserRepository, final SpringMongoJobRepository springMongoJobRepository, final UserMapper userMapper, JobMapper jobMapper) {
        this.springMongoUserRepository = springMongoUserRepository;
        this.springMongoJobRepository = springMongoJobRepository;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springMongoUserRepository.findByEmail(email).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByEmailOrUsername(String email, String username) {
        return springMongoUserRepository.findByEmailOrUsername(email, username).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springMongoUserRepository.findById(id).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findByIdWithJobs(UUID id) {
        return findById(id).map(u -> u.setJobs(jobMapper.toDomain(springMongoJobRepository.findByUserId(u.getId(), null))));
    }

    @Override
    public User save(User user) {
        return this.userMapper.toDomain(springMongoUserRepository.save(this.userMapper.toEntity(user)));
    }
}
