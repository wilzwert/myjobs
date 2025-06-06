package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.mongodb.bulk.BulkWriteResult;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobState;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.UserView;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkDataSaveResult;
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
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Wilhelm Zwertvaegher
 */
@Component
public class UserDataManagerAdapter implements UserDataManager {
    private final MongoUserRepository mongoUserRepository;
    private final MongoJobRepository mongoJobRepository;
    private final AggregationService aggregationService;
    private final UserMapper userMapper;
    private final JobMapper jobMapper;
    private final MongoRefreshTokenRepository mongoRefreshTokenRepository;
    private final MongoTemplate mongoTemplate;

    public UserDataManagerAdapter(final MongoUserRepository mongoUserRepository, final MongoJobRepository mongoJobRepository, final AggregationService aggregationService, final UserMapper userMapper, JobMapper jobMapper, MongoRefreshTokenRepository mongoRefreshTokenRepository, MongoTemplate mongoTemplate) {
        this.mongoUserRepository = mongoUserRepository;
        this.mongoJobRepository = mongoJobRepository;
        this.aggregationService = aggregationService;
        this.userMapper = userMapper;
        this.jobMapper = jobMapper;
        this.mongoRefreshTokenRepository = mongoRefreshTokenRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserView> findView(DomainSpecification specifications) {
        Aggregation aggregation = aggregationService.createAggregation(specifications);
        return userMapper.toDomainView(aggregationService.aggregate(aggregation, "users", MongoUser.class));
    }

    @Override
    public Map<UserId, User> findMinimal(DomainSpecification specifications) {
        Aggregation aggregation = aggregationService.createAggregation(specifications);
        return userMapper.toDomain(aggregationService.aggregate(aggregation, "users", MongoUser.class))
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return getFullUser(mongoUserRepository.findByEmail(email));
    }

    @Override
    public Optional<User> findMinimalByEmailValidationCode(String code) {
        return mongoUserRepository.findByEmailValidationCode(code).map(userMapper::toDomain);
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
        return mongoUserRepository.findById(id.value()).map(userMapper::toDomainView);
    }

    @Override
    public Optional<User> findMinimalByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findMinimalByEmail(String email) {
        return mongoUserRepository.findByEmail(email).map(userMapper::toDomain).or(Optional::empty);
    }

    private Optional<User> getFullUser(Optional<MongoUser> user) {
        return user.map(u -> userMapper.toDomain(u).completeWith(jobMapper.toDomain(mongoJobRepository.findByUserId(u.getId()))));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return getFullUser(mongoUserRepository.findById(id.value()));
    }

    @Override
    public Optional<User> findMinimalById(UserId id) {
        return mongoUserRepository.findById(id.value()).map(userMapper::toDomain);
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
        return userMapper.toDomain(mongoUserRepository.save(this.userMapper.toEntity(user)));
    }


    @Override
    @Transactional
    public User saveUserAndJob(User user, Job job) {
        mongoJobRepository.save(this.jobMapper.toEntity(job));
        return save(user);
    }

    @Override
    @Transactional
    public User deleteJobAndSaveUser(User user, Job job) {
        mongoJobRepository.delete(this.jobMapper.toEntity(job));
        return userMapper.toDomain(this.mongoUserRepository.save(this.userMapper.toEntity(user)));
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
    public BulkDataSaveResult saveAll(Set<User> users) {
        // we chose to throw an exception because it seems like something went wrong if someone tries to save an empty set
        if(users.isEmpty()) {
            throw new IllegalArgumentException("users must not be empty");
        }

        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, MongoUser.class);

        List<MongoUser> mongoUsers = userMapper.toEntity(users.stream().toList());
        for(MongoUser user : mongoUsers) {
            Update update = new Update();
            update.set("jobFollowUpReminderSentAt", user.getJobFollowUpReminderSentAt());
            bulkOps.updateOne(Query.query(Criteria.where("_id").is(user.getId())), update);
        }

        BulkWriteResult result = bulkOps.execute();
        return new BulkDataSaveResult(users.size(), result.getModifiedCount(), result.getInsertedCount(), result.getDeletedCount());
    }

    @Override
    public List<JobState> getJobsState(User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(user.getId().value()));
        query.fields().include("status").include("updatedAt").include("statusUpdatedAt");
        List<MongoJob> projection = mongoTemplate.find(query, MongoJob.class);
        return projection.stream().map(j -> new JobState(j.getStatus(), j.getUpdatedAt(), j.getStatusUpdatedAt())).collect(Collectors.toList());
    }
}