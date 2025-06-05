package com.wilzwert.myjobs.infrastructure.api.rest.dto.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class UpdateJobDtoFactoryTest {

    private UpdateJobDtoFactory factory;

    @BeforeEach
    void setUp() {
        factory = new UpdateJobDtoFactory(new ObjectMapper());
    }

    @Test
    void whenSingleFieldProvided_thenShouldCreateSpecificDto() {
        Map<String, Object> request = Map.of("title", "New job title");

        UpdateJobDto dto = factory.createUpdateJobDto(request);

        assertThat(dto)
            .isInstanceOf(UpdateJobTitleRequest.class);

        UpdateJobTitleRequest titleRequest = (UpdateJobTitleRequest) dto;
        assertThat(titleRequest.getTitle()).isEqualTo("New job title");
    }

    @Test
    void whenMultipleFieldsProvided_thenShouldCreateGeneralUpdateJobRequest() {
        Map<String, Object> request = Map.of(
            "title", "New title",
            "description", "New description"
        );

        UpdateJobDto dto = factory.createUpdateJobDto(request);

        assertThat(dto)
            .isInstanceOf(UpdateJobRequest.class);

        UpdateJobRequest updateRequest = (UpdateJobRequest) dto;
        assertThat(updateRequest.getTitle()).isEqualTo("New title");
        assertThat(updateRequest.getDescription()).isEqualTo("New description");
    }

    @Test
    void whenEmptyMapProvided_thenShouldThrowException() {
        assertThatThrownBy(() -> factory.createUpdateJobDto(Map.of()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("cannot be empty");
    }

    @Test
    void whenUnknownFieldProvided_thenShouldThrowException() {
        Map<String, Object> request = Map.of("unknownField", "value");

        assertThatThrownBy(() -> factory.createUpdateJobDto(request))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessageContaining("Unknown key: unknownField");
    }
}