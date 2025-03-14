package com.wilzwert.myapps.infrastructure.persistence.mongo.repository;


import com.wilzwert.myapps.domain.model.User;
import com.wilzwert.myapps.domain.ports.driven.UserRepository;
import com.wilzwert.myapps.infrastructure.persistence.mongo.entity.MongoUser;
import com.wilzwert.myapps.infrastructure.persistence.mongo.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        Optional<MongoUser> user = springMongoUserRepository.findByEmail(email);
        return user.map(userMapper::toUser).or(Optional::empty);
    }

    @Override
    public User save(User user) {
        return this.userMapper.toUser(springMongoUserRepository.save(this.userMapper.toMongoUser(user)));
    }
}
