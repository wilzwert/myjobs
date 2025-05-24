package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;


import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Wilhelm Zwertvaegher
 */

public class JobRatingReadConverter implements Converter<Integer, JobRating> {
    @Override
    public JobRating convert(Integer source) {
        return JobRating.of(source);
    }
}
