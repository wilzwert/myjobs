package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.shared.querying.criteria.DomainQueryingCriterion;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriterionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DomainQueryingConverterTest {

    private DomainQueryingConverter domainQueryingConverter;

    @BeforeEach
    void setup() {
        domainQueryingConverter = new DomainQueryingConverter();
    }

    @Test
    void whenDomainCriteriaNotSupported_thenShouldThrowException() {
        class UnsupportedQueryingCriterion extends DomainQueryingCriterion {
            UnsupportedQueryingCriterion() {
                super();
            }
        }

        var unsupported = new UnsupportedQueryingCriterion();

        assertThrows(
            UnsupportedDomainCriterionException.class,
            () -> domainQueryingConverter.domainOperationToAggregationOperation(unsupported)
        );
    }
}