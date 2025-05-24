package com.wilzwert.myjobs.core.domain.model.user.ports.driven;


import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;

import java.util.Set;

/**
 * @author Wilhelm Zwertvaegher
 */

public interface JobReminderMessageProvider {
    void send(User user, Set<Job> jobs);
}
