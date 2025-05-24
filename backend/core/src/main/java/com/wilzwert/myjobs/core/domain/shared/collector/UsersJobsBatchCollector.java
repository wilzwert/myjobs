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
 * @author Wilhelm Zwertvaegher
 *  A collector for batch operations on Users and Jobs, as in "for each User we have a list of Jobs"
 *  This is why the bat chProcessing function expects a Map<User, SortedSet<Job>> parameter
 *  Please note that the batchSize concerns users, e.g, batchSize users will be handled, whatever number of Jobs each have
 *  Warning : Jobs list MUST be sorted by userId BEFORE using this collector.
 *  This is very important because otherwise the appropriate grouping user -> jobs would not be guaranteed
 */

public class UsersJobsBatchCollector<T> implements Collector<Job, Map<UserId, SortedSet<Job>>, List<T>> {


    /**
     * The function used to find users based on a list of UserId
     * This must be passed as a constructor parameter when instantiating this Collector
     */
    private final Function<List<UserId>, Map<UserId, User>> findUsersFunction;

    /**
     * The function used to do the batch processing
     */
    private final Function<Map<User, Set<Job>>, T> batchProcessing;

    /**
     * Use for state keeping while accumulating
     */
    private UserId currentUserId;

    /**
     * Size of the users chunk passed to the batch processing
     */
    private final int batchSize;

    /**
     * The results of the batch processing
     */
    private final List<T> results = new ArrayList<>();

    public UsersJobsBatchCollector(Function<List<UserId>, Map<UserId, User>> findUsersFunction, Function<Map<User, Set<Job>>, T> batchProcessing, int batchSize) {
        this.findUsersFunction = findUsersFunction;
        this.batchProcessing = batchProcessing;
        this.batchSize = batchSize;
    }

    @Override
    public Supplier<Map<UserId, SortedSet<Job>>> supplier() {
        return HashMap::new;
    }

    private Map<User, Set<Job>> load(Map<UserId, SortedSet<Job>> userIdsToJobs) {
        Map<UserId, User> users = findUsersFunction.apply(userIdsToJobs.keySet().stream().toList());
        return users
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, e -> userIdsToJobs.get(e.getKey())));

    }

    @Override
    public BiConsumer<Map<UserId, SortedSet<Job>>, Job> accumulator() {
        return (userIdsToJobs, job) -> {
            // detecting a change of user in the stream triggers the processing
            // (only after at least first job / user has been handled)
            if(currentUserId != null && !job.getUserId().equals(currentUserId) && userIdsToJobs.size() >= batchSize) {
                // pass a copy of the current supplier to the batchProcessing
                final Map<UserId, SortedSet<Job>> copy =  new HashMap<>(userIdsToJobs);
                results.add(batchProcessing.apply(load(copy)));

                // reset current supplier
                userIdsToJobs.clear();
            }
            userIdsToJobs.computeIfAbsent(job.getUserId(), k -> new TreeSet<>(Comparator.comparing(Job::getUpdatedAt))).add(job);
            currentUserId = job.getUserId();
        };
    }

    @Override
    public BinaryOperator<Map<UserId, SortedSet<Job>>> combiner() {
        return (set1, set2) -> { throw new UnsupportedOperationException("Parallel processing is not supported"); };
    }

    @Override
    public Function<Map<UserId, SortedSet<Job>>, List<T>> finisher() {
        return userIdsToJobs -> {
            if(!userIdsToJobs.isEmpty()) {
                // userIdsToJobs not cleared, apply batch processing to the remaining entries
                results.add(batchProcessing.apply(load(userIdsToJobs)));
            }
            return results;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
