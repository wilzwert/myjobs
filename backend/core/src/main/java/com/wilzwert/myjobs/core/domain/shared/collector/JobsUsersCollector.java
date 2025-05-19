package com.wilzwert.myjobs.core.domain.shared.collector;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This collector handles a List of job and builds a Map<User, Set<Job>> by calling the loading function passed as
 * a parameter
 *
 * @author Wilhelm Zwertvaegher
 * Date:14/05/2025
 * Time:15:50
 */

public class JobsUsersCollector implements Collector<Job, Map<UserId, Set<Job>>, Map<User, Set<Job>>> {

    private final Function<List<UserId>, Map<UserId, User>> findFunction;

    public JobsUsersCollector(Function<List<UserId>, Map<UserId, User>> findFunction) {
        this.findFunction = findFunction;
    }

    @Override
    public Supplier<Map<UserId, Set<Job>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<UserId, Set<Job>>, Job> accumulator() {
        return (Map<UserId, Set<Job>> userIdsToJobs, Job job) -> {
            userIdsToJobs.computeIfAbsent(job.getUserId(), k -> new HashSet<>()).add(job);
        };
    }

    @Override
    public BinaryOperator<Map<UserId, Set<Job>>> combiner() {
        return (set1, set2) -> { throw new UnsupportedOperationException("Parallel processing is not supported"); };
    }

    @Override
    public Function<Map<UserId, Set<Job>>, Map<User, Set<Job>>> finisher() {
        return userIdsToJobs ->
                findFunction.apply(userIdsToJobs.keySet().stream().toList())
                        .entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getValue, e -> userIdsToJobs.get(e.getKey())));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}