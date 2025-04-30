package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.util.StdConverter;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:45
 */

@Component
class JobRatingConverter extends StdConverter<String, JobRating> {

    @Override
    public JobRating convert(String s) throws NumberFormatException {
        return JobRating.of(Integer.parseInt(s));
    }
}
