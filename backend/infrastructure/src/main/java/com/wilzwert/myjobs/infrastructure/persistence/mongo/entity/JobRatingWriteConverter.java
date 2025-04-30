package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;


import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Wilhelm Zwertvaegher
 * Date:22/04/2025
 * Time:15:29
 */

public class JobRatingWriteConverter implements Converter<JobRating, Integer> {
    @Override
    public Integer convert(JobRating source) {
        return source.getValue();
    }
}
