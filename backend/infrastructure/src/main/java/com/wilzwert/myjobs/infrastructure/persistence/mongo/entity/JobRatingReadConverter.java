package com.wilzwert.myjobs.infrastructure.persistence.mongo.entity;


import com.wilzwert.myjobs.core.domain.model.JobRating;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Wilhelm Zwertvaegher
 * Date:22/04/2025
 * Time:15:29
 */

public class JobRatingReadConverter implements Converter<Integer, JobRating> {
    @Override
    public JobRating convert(Integer source) {
        return JobRating.of(source);
    }
}
