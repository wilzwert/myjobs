package com.wilzwert.myjobs.core.domain.shared.specification;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainSpecificationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainSpecificationTest {

    @Test
    void testInCriteria() {
        List<String> values = List.of("status1", "status2", "status3");
        DomainSpecification.In<String> inCriteria = DomainSpecification.in("status", values);

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
        DomainSpecification.Eq<String> eqCriteria = DomainSpecification.eq("status", value);

        // check field
        assertEquals("status", eqCriteria.getField());

        // check value
        assertEquals("active", eqCriteria.getValue());
    }

    @Test
    void testLtCriteria() {
        Integer value = 30;
        DomainSpecification.Lt<Integer> ltCriteria = DomainSpecification.lt("age", value);

        // check field
        assertEquals("age", ltCriteria.getField());

        // check value
        assertEquals(30, ltCriteria.getValue());
    }

    @Test
    void testContains() {
        List<DomainSpecification> specs = List.of(DomainSpecification.eq("lastname", "bobby"),
                DomainSpecification.lt("createdAt", Instant.now()));

        DomainSpecification userSpec = DomainSpecification.or(
            specs
        );

        assertTrue(userSpec.contains(DomainSpecification.Lt.class));
        assertTrue(userSpec.contains(DomainSpecification.Or.class));
        assertFalse(userSpec.contains(DomainSpecification.And.class));
    }

    @Test
    void whenTryingToNestFullSpecification_thenShouldThrowException() {
        List<DomainSpecification> specs = List.of(
                DomainSpecification.eq("title", "bobby"),
                DomainSpecification.lt("createdAt", Instant.now()));

        // DomainSpecification.JobFollowUpToRemind being a FullSpecification, it cannot be composed
        var list = List.of(
                DomainSpecification.or(specs),
                DomainSpecification.JobFollowUpToRemind(Instant.now())
        );
        assertThrows(DomainSpecificationException.class, () ->
            DomainSpecification.and(list)
        );
    }

    @Test
    void testDefaultSpecSort() {
        var spec = DomainSpecification.eq("title", "bobby");
        assertEquals("createdAt", spec.getSort().getFirst().getFieldName());
        assertEquals(DomainSpecification.SortDirection.DESC, spec.getSort().getFirst().getSortDirection());
    }

    @Test
    void testSpecSort() {
        var spec = DomainSpecification.eq("title", "bobby");
        DomainSpecification.applySort(spec, DomainSpecification.sort("field", DomainSpecification.SortDirection.ASC));
        assertEquals("field", spec.getSort().getFirst().getFieldName());
        assertEquals(DomainSpecification.SortDirection.ASC, spec.getSort().getFirst().getSortDirection());
    }

    @Test
    void testSpecSortList() {
        var spec = DomainSpecification.eq("title", "bobby");
        DomainSpecification.applySort(spec, List.of(
                DomainSpecification.sort("field", DomainSpecification.SortDirection.ASC),
                DomainSpecification.sort("otherField"),
                DomainSpecification.sort("thirdField,desc")
        ));
        assertEquals(3, spec.getSort().size());
        assertEquals("field", spec.getSort().getFirst().getFieldName());
        assertEquals(DomainSpecification.SortDirection.ASC, spec.getSort().getFirst().getSortDirection());

        assertEquals("otherField", spec.getSort().get(1).getFieldName());
        assertEquals(DomainSpecification.SortDirection.ASC, spec.getSort().get(1).getSortDirection());

        assertEquals("thirdField", spec.getSort().get(2).getFieldName());
        assertEquals(DomainSpecification.SortDirection.DESC, spec.getSort().get(2).getSortDirection());
    }

    @Test
    void testSpecSortFromString() {
        var spec = DomainSpecification.eq("title", "bobby");
        DomainSpecification.applySort(spec, DomainSpecification.sort("field,desc"));
        assertEquals("field", spec.getSort().getFirst().getFieldName());
        assertEquals(DomainSpecification.SortDirection.DESC, spec.getSort().getFirst().getSortDirection());
    }

    @Test
    void testSpecSortFromStringWithoutDirection() {
        var spec = DomainSpecification.eq("title", "bobby");
        DomainSpecification.applySort(spec, DomainSpecification.sort("field"));
        assertEquals("field", spec.getSort().getFirst().getFieldName());
        assertEquals(DomainSpecification.SortDirection.ASC, spec.getSort().getFirst().getSortDirection());
    }

    @Test
    void whenNoValueAndNoValueClass_thenEqShouldThrowException() {
        var eq = DomainSpecification.eq("title", null);
        assertThrows(IllegalStateException.class, eq::getValueClass);
    }

    @Test
    void whenValueAndNoValueClass_thenEqShouldInferValueClass() {
        var eq = DomainSpecification.eq("title","1");
        assertEquals(String.class, eq.getValueClass());
    }

    @Test
    void whenNoValueAndNoValueClass_thenLtShouldThrowException() {
        var lt = DomainSpecification.lt("title", null);
        assertThrows(IllegalStateException.class, lt::getValueClass);
    }

    @Test
    void whenValueAndNoValueClass_thenLtShouldInferValueClass() {
        var lt = DomainSpecification.lt("title","1");
        assertEquals(String.class, lt.getValueClass());
    }



    @Test
    void whenNoValuesAndNoValueClass_thenInShouldThrowException() {
        var in = DomainSpecification.in("title", Collections.emptyList());
        assertThrows(IllegalStateException.class, in::getValueClass);
    }

    @Test
    void whenValuesAndNoValueClass_thenShouldInferValueClass() {
        var in = DomainSpecification.in("title", List.of("1"));
        assertEquals(String.class, in.getValueClass());
    }
}
