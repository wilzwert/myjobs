package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import tools.jackson.databind.ValueDeserializer;


public class JobRatingDeserializer extends ValueDeserializer<JobRating> {
    @Override
    public JobRating deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        int value = p.getIntValue();
        return JobRating.of(value);
    }
}