package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationErrors;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:05/06/2025
 * Time:10:20
 *
 * Factory used to create appropriate DTO based on Job patch request
 */

@Service
public class UpdateJobDtoFactory {

    private static final Map<String, Class<? extends UpdateJobDto>> keyToDto = Map.of(
        "rating", UpdateJobRatingRequest.class,
        "title", UpdateJobTitleRequest.class,
        "description", UpdateJobDescriptionRequest.class,
        "profile", UpdateJobProfileRequest.class,
        "company", UpdateJobCompanyRequest.class,
        "comment", UpdateJobCommentRequest.class,
        "url", UpdateJobUrlRequest.class,
        "salary", UpdateJobSalaryRequest.class
    );

    private final ObjectMapper objectMapper;

    UpdateJobDtoFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UpdateJobDto createUpdateJobDto(Map<String, Object> requestFields) {
        if (requestFields.isEmpty()) {
            throw new IllegalArgumentException("Request fields cannot be empty");
        }

        if(requestFields.size() > 1) {
            return objectMapper.convertValue(requestFields, UpdateJobRequest.class);
        }
        // we know we only have one key, so it's safe to get it
        String key = requestFields.keySet().iterator().next();

        if(!keyToDto.containsKey(key)) {
            throw new UnsupportedOperationException("Unknown key: " + key);
        }

        Map<String, Object> enrichedRequestFields = new HashMap<>(requestFields);
        enrichedRequestFields.put("field", key);
        enrichedRequestFields.put("value", requestFields.get(key));
        try {
            return objectMapper.convertValue(enrichedRequestFields, keyToDto.get(key));
        }
        catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof JsonMappingException ife) {
                List<JsonMappingException.Reference> path = ife.getPath();
                if (!path.isEmpty()) {
                    String fieldName = path.getFirst().getFieldName();
                    ValidationErrors errors = new ValidationErrors();
                    errors.add(new ValidationError(fieldName, ErrorCode.INVALID_VALUE));
                    throw new ValidationException(errors);
                }
            }
            throw new IllegalArgumentException(ErrorCode.VALIDATION_FAILED.name(), e);
        }
    }
}