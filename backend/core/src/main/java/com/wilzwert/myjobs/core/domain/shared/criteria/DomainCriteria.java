package com.wilzwert.myjobs.core.domain.shared.criteria;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Criteria for data retrieval
 * Criteria are design to be passed to infra data loading services
 * This allows to keep infra agnostic of business rules to filter data retrieval
 * e.g. to load "follow up late" Jobs, we have to filter jobs
 * based on their status update date and user preferences, but this should remain in the domain
 * in case these rules change later
 * As of no we only beed  In, Eq and Lt
 *
 */
public abstract class DomainCriteria {

    private final String field;
    private DomainCriteria(String field) {
        this.field = field;
    }
    public String getField() {
        return field;
    }

    public static final class In<T> extends DomainCriteria {

        private final  List<T> values;

        public In(String field, List<T> values) {
            super(field);
            this.values = values;
        }

        public List<T> getValues() {
            return values;
        }
    }

    public static final class Eq<T> extends DomainCriteria {
        private final T value;
        public Eq(String field, T value) {
            super(field);
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public static final class Lt<T> extends DomainCriteria {
        private final T value;
        public Lt(String field, T value) {
            super(field);
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }
}
