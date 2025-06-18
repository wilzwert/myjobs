package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;

import java.io.IOException;

public class JobRatingDeserializer extends JsonDeserializer<JobRating> {
    @Override
    public JobRating deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int value = p.getIntValue();
        return JobRating.of(value);
    }
}