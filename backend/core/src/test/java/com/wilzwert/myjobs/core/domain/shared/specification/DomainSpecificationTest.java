package com.wilzwert.myjobs.core.domain.shared.specification;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainSpecificationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainSpecificationTest {

    @Test
    void testInCriteria() {
        List<String> values = List.of("status1", "status2", "status3");
        DomainSpecification.In<String> inCriteria = DomainSpecification.In("status", values);

        // check the field is initialized
        assertEquals("status", inCriteria.getField());

        // check values
        assertNotNull(inCriteria.getValues());
        assertEquals(3, inCriteria.getValues().size());
        assertTrue(inCriteria.getValues().contains("status1"));
        assertTrue(inCriteria.getValues().contains("status2"));
        assertTrue(inCriteria.getValues().contains("status3"));
    }

    @Test
    void testEqCriteria() {
        String value = "active";
        DomainSpecification.Eq<String> eqCriteria = DomainSpecification.Eq("status", value);

        // check field
        assertEquals("status", eqCriteria.getField());

        // check value
        assertEquals("active", eqCriteria.getValue());
    }

    @Test
    void testLtCriteria() {
        Integer value = 30;
        DomainSpecification.Lt<Integer> ltCriteria = DomainSpecification.Lt("age", value);

        // check field
        assertEquals("age", ltCriteria.getField());

        // check value
        assertEquals(30, ltCriteria.getValue());
    }

    @Test
    void testContains() {
        List<DomainSpecification> specs = List.of(DomainSpecification.Eq("lastname", "bobby"),
                DomainSpecification.Lt("createdAt", Instant.now()));

        DomainSpecification userSpec = DomainSpecification.Or(
            specs
        );

        assertTrue(userSpec.contains(DomainSpecification.Lt.class));
        assertTrue(userSpec.contains(DomainSpecification.Or.class));
        assertFalse(userSpec.contains(DomainSpecification.And.class));
    }

    @Test
    void whenTryingToNestFullSpecification_thenShouldThrowException() {
        List<DomainSpecification> specs = List.of(
                DomainSpecification.Eq("title", "bobby"),
                DomainSpecification.Lt("createdAt", Instant.now()));

        // DomainSpecification.JobFollowUpToRemind being a FullSpecification, it cannot be composed
        assertThrows(DomainSpecificationException.class, () -> {
            DomainSpecification.And(
                List.of(
                    DomainSpecification.Or(specs),
                    DomainSpecification.JobFollowUpToRemind(Instant.now())
                )
            );
        });
    }
}
