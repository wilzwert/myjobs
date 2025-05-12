package com.wilzwert.myjobs.core.domain.shared.querying.criteria;

import com.wilzwert.myjobs.core.domain.shared.querying.DomainQueryingOperation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Criteria for data retrieval
 * Criteria are design to be passed to infra data loading services
 * This allows to keep infra agnostic of business rules to filter data retrieval
 * e.g. to load "follow up late" Jobs, we have to filter jobs
 * based on their status update date and user preferences, but this should remain in the domain
 * in case these rules change later
 * As of no we only beed  In, Eq, Lt and Or
 *
 */
public abstract class DomainQueryingCriterion extends DomainQueryingOperation {
    protected DomainQueryingCriterion() {}

    private abstract static class FieldQueryingCriterion extends DomainQueryingCriterion {
        private final String field;
        private FieldQueryingCriterion(String field) {
            this.field = field;
        }
        public String getField() {
            return field;
        }
    }

    private abstract static class ConditionQueryingCriterion extends DomainQueryingCriterion {
        private final List<DomainQueryingCriterion> criteriaList;
        private ConditionQueryingCriterion(DomainQueryingCriterion... criteriaList) {
            this.criteriaList = new ArrayList<>();
        }

        public List<DomainQueryingCriterion> getCriteriaList() {
            return criteriaList;
        }
    }

    public static final class In<T> extends FieldQueryingCriterion {

        private final  List<T> values;

        public In(String field, List<T> values) {
            super(field);
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
    }

    public static final class Eq<T> extends FieldQueryingCriterion {
        private final T value;
        public Eq(String field, T value) {
            super(field);
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public static final class Lt<T> extends FieldQueryingCriterion {
        private final T value;
        public Lt(String field, T value) {
            super(field);
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public static final class Or extends ConditionQueryingCriterion {
        public Or(DomainQueryingCriterion... criteriaList) {
            super(criteriaList);
        }
    }

    // this criterion is very specific, and should be understood and applied by the infra
    // because it would require lots of code to remain generic without inferring how the infra persistence works
    // as though it may seem like a business rules leak in the infra, it is explicit and acceptable in our context
    public static final class UserJobFollowUpReminderThreshold extends DomainQueryingCriterion {
        private final Instant referenceInstant;

        public UserJobFollowUpReminderThreshold(Instant referenceInstant) {
            this.referenceInstant = referenceInstant;
        }

        public Instant getReferenceInstant() {
            return referenceInstant;
        }
    }
}
