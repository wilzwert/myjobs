package com.wilzwert.myjobs.core.domain.shared.criteria;

import com.wilzwert.myjobs.core.domain.shared.querying.criteria.DomainQueryingCriterion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainQueryingCriterionTest {

    @Test
    void testInCriteria() {
        List<String> values = List.of("status1", "status2", "status3");
        DomainQueryingCriterion.In<String> inCriteria = new DomainQueryingCriterion.In<>("status", values);

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
        DomainQueryingCriterion.Eq<String> eqCriteria = new DomainQueryingCriterion.Eq<>("status", value);

        // check field
        assertEquals("status", eqCriteria.getField());

        // check value
        assertEquals("active", eqCriteria.getValue());
    }

    @Test
    void testLtCriteria() {
        Integer value = 30;
        DomainQueryingCriterion.Lt<Integer> ltCriteria = new DomainQueryingCriterion.Lt<>("age", value);

        // check field
        assertEquals("age", ltCriteria.getField());

        // check value
        assertEquals(30, ltCriteria.getValue());
    }
}
