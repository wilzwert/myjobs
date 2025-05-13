package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriterionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DomainSpecificationConverterTest {

    private DomainSpecificationConverter domainSpecificationConverter;

    @BeforeEach
    void setup() {
        domainSpecificationConverter = new DomainSpecificationConverter();
    }

    @Test
    void whenDomainCriteriaNotSupported_thenShouldThrowException() {
        class UnsupportedSpecification extends DomainSpecification<User> {
            UnsupportedSpecification() {
                super();
            }
        }

        var unsupported = new UnsupportedSpecification();

        assertThrows(
            UnsupportedDomainCriterionException.class,
            () -> domainSpecificationConverter.domainSpecificationToAggregationOperation(unsupported)
        );
    }
}