package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.util.StdConverter;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 */

@Component
class JobRatingConverter extends StdConverter<String, JobRating> {

    @Override
    public JobRating convert(String s) throws NumberFormatException {
        return JobRating.of(Integer.parseInt(s));
    }
}
