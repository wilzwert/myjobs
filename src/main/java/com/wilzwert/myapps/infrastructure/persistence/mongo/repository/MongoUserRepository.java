package com.wilzwert.myapps.infrastructure.persistence.mongo.repository;


import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.domain.ports.driven.UserRepository;
import com.wilzwert.myapps.infrastructure.persistence.mongo.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public MongoUserRepository(final SpringMongoUserRepository springMongoUserRepository, final UserMapper userMapper) {
        this.springMongoUserRepository = springMongoUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springMongoUserRepository.findByEmail(email).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springMongoUserRepository.findById(id).map(userMapper::toDomain).or(Optional::empty);
    }

    @Override
    public User save(User user) {
        return this.userMapper.toDomain(springMongoUserRepository.save(this.userMapper.toEntity(user)));
    }
}
