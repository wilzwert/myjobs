package com.wilzwert.myapps.domain.ports.driven;


import com.wilzwert.myapps.domain.model.Job;

import java.util.Optional;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:29
 */
public interface JobRepository {
    Optional<Job> findById(String id);

    Job save(Job job);
}
