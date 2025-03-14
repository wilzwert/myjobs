package com.wilzwert.myapps.domain.ports.driving;


import com.wilzwert.myapps.domain.model.Job;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:25
 */
public interface CreateJobUseCase {
    public Job createJob(Job job);
}
