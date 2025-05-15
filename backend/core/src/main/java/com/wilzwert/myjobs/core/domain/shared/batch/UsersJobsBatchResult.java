package com.wilzwert.myjobs.core.domain.shared.batch;


import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.shared.bulk.BulkServiceSaveResult;

import java.util.Map;
import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:01
 */

public class UsersJobsBatchResult {

    private final Map<User, Integer> usersToJobsCount;

    private final BulkServiceSaveResult bulkServiceSaveResult;

    public UsersJobsBatchResult(Map<User, Integer> usersToJobsCount) {
        this.usersToJobsCount = usersToJobsCount;
        this.bulkServiceSaveResult = null;
    }

    public UsersJobsBatchResult(Map<User, Integer> usersToJobsCount, BulkServiceSaveResult bulkServiceSaveResult) {
        this.usersToJobsCount = usersToJobsCount;
        this.bulkServiceSaveResult = bulkServiceSaveResult;
    }

    public int getUsersCount() {
        return usersToJobsCount.size();
    }

    public int getJobsCount() {
        return  usersToJobsCount.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Optional<BulkServiceSaveResult> getBulkServiceResult() {
        return Optional.ofNullable(bulkServiceSaveResult);
    }
}
