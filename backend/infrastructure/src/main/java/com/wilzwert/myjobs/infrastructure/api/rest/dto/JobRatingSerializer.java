package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import com.fasterxml.jackson.databind.util.StdConverter;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import org.springframework.stereotype.Component;

/**
 * @author Wilhelm Zwertvaegher
 */

@Component
class JobRatingSerializer extends StdConverter<JobRating, String> {

    @Override
    public String convert(JobRating jr) throws NumberFormatException {
        return String.valueOf(jr.getValue());
    }
}
