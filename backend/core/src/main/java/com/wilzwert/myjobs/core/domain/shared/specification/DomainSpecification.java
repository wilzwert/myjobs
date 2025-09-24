package com.wilzwert.myjobs.core.domain.shared.specification;

import com.wilzwert.myjobs.core.domain.shared.exception.DomainSpecificationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * <p>Specification for data querying / filtering</p>
 * <p>These specification are meant to be converted by infra in any form suitable to retrieve data</p>
 * <p>Specifications must be parameterized at least by an aggregate / entity class which is the domain model "target"</p>
 * <p>If we have to name fields, they MUST be the names of aggregates properties ; as the domain should have no idea how persistence
 * is handled, it makes no sense to use field names prefixed by their "collection" or "table",
 * as it would assume that some kind of joins are made, which is not guaranteed at all
 * </p>
 * <p>Important : of course, this should be seen more as an exercise / experiment than a mature / complete solution :
 * - it works only for very simple conditions
 * - it is very naive after all
 * </p>
 * <p>Use example :
 * if some domain use case had a business rule where it has to load Jobs that are currently in JobStatus.PENDING,
 * have been created more than 7 days ago and have been updated more than 3 days ago, we could pass something like this :<br>
 * jobService.findPaginated(DomainSpecification.And(List.of(
 *  DomainSpecification.Eq("status", JobStatus.PENDING),*
 *  DomainSpecification.Lt("createdAt", Instant.now() - 7 * 86_400_000),
 *  DomainSpecification.Lt("updatedAt", Instant.now() - 3 * 86_400_000)
 *  )), 0, 10)
 * instead of adding a findByStatusPendingAndCreatedMoreThan7DaysAgoAndUpdatedMoreThan3DaysAgo method to our JobDataManager interface
 * </p>
 * On the other side, some simple rules would be very difficult to implement using these simple Specification
 * For example, to load all "follow up late" Jobs, we have to filter jobs based on their status, status update date and related user preferences.
 * This would be difficult to model using specification, and it would feel like reinventing the wheel, over-assuming how the infra
 * persistence handles querying and how its data is stored : we would be tempted to create some sort of objects representing
 * joins, relations, projections...
 * </p>
 * <p>In that case we can create explicit Specification, i.e. "contracts" the infra must then understand or implement, such as :
 * DomainSpecification.UserJobFollowUpReminderThreshold
 * With use of the contains method, infra could also change querying / retrieval strategy
 * Of course it alsp remains possible and maybe more suitable to add methods to our data retrieval services interfaces,
 * but it's conceptually interesting to have standalone specs, separated from services
 * because it makes them easily reusable.
 * </p>
 */
public abstract class DomainSpecification {

    private final List<DomainSpecification.Sort> sort = new ArrayList<>();

    public final List<DomainSpecification.Sort> getSort() {
        if(sort.isEmpty()) {
            // by convention, sort always defaults to createdAt desc
            return List.of(new Sort("createdAt", SortDirection.DESC));
        }
        return sort;
    }

    public final void sortBy(DomainSpecification.Sort sort) {
        this.sort.add(sort);
    }

    public static <T extends DomainSpecification> T applySort(T spec, Sort sort) {
        spec.sortBy(sort);
        return spec;
    }

    public static <T extends DomainSpecification> T applySort(T spec, List<Sort> sortList) {
        for (Sort sort: sortList) {
            spec.sortBy(sort);
        }
        return spec;
    }

    /**
     * In some cases, infra may have to check if a certain specification exists in the current hierarchy
     * to change implementation strategy
     * @param classToFind the class to find in the current specification hierarchy
     * @return true if class found
     */
    public boolean contains(Class<?> classToFind) {
        return classToFind.equals(getClass());
    }

    /**
     * Sorting
     *
     */
    public enum SortDirection {
        ASC, DESC
    }

    public static Sort sort(String sort) {
        return new Sort(sort);
    }

    public static Sort sort(String fieldName, SortDirection sortDirection) {
        return new Sort(fieldName, sortDirection);
    }

    public static class Sort extends DomainSpecification {
        private final String fieldName;
        private final SortDirection sortDirection;

        Sort(String fieldName, SortDirection sortDirection) {
            this.fieldName = fieldName;
            this.sortDirection = sortDirection;
        }

        Sort(String sort) {
            String[] parts = sort.split(",");
            this.fieldName = parts[0];
            this.sortDirection = parts.length < 2 || parts[1].equals("asc") ? SortDirection.ASC : SortDirection.DESC;
        }

        public String getFieldName() {
            return fieldName;
        }

        public SortDirection getSortDirection() {
            return sortDirection;
        }
    }

    /**
     * Field specs concern only one field
     */
    public abstract static class FieldSpecification<V> extends DomainSpecification {
        private final String field;
        private final Class<V> valueClass;

        private FieldSpecification(String field, Class<V> valueClass) {
            this.field = field;
            this.valueClass = valueClass;
        }

        public String getField() {
            return field;
        }

        public Class<V> getValueClass() {
            return valueClass;
        }
    }

    public abstract static class FieldSpecificationWithSingleValue<V> extends FieldSpecification<V> {
        private final V value;

        protected FieldSpecificationWithSingleValue(String field, V value, Class<V> valueClass) {
            super(field, valueClass);
            this.value = value;
        }

        protected FieldSpecificationWithSingleValue(String field, V value) {
            this(field, value, null);
        }

        public V getValue() {
            return value;
        }

        @Override
        public Class<V> getValueClass() {
            Class<V> valueClass = super.getValueClass();
            if(valueClass != null) {
                return valueClass;
            }

            if(value == null) {
                throw new IllegalStateException("Cannot determine valueClass: value is empty and valueClass is null");
            }

            @SuppressWarnings("unchecked")
            Class<V> inferredClass = (Class<V>) value.getClass();
            return inferredClass;
        }
    }

    public abstract static class FieldSpecificationWithValuesList<V> extends FieldSpecification<V> {
        private final  List<V> values;

        protected FieldSpecificationWithValuesList(String field, List<V> values, Class<V> valueClass) {
            super(field, valueClass);
            this.values = values;
        }

        protected FieldSpecificationWithValuesList(String field, List<V> values) {
            this(field, values, null);
        }

        public List<V> getValues() {
            return values;
        }

        @Override
        public Class<V> getValueClass() {
            Class<V> valueClass = super.getValueClass();
            if(valueClass != null) {
                return valueClass;
            }
            if (values == null || values.isEmpty()) {
                throw new IllegalStateException("Cannot determine valueClass: values are empty and valueClass is null");
            }
            V firstValue = values.getFirst();
            if (firstValue == null) {
                throw new IllegalStateException("Cannot determine valueClass: first value is null");
            }
            @SuppressWarnings("unchecked")
            Class<V> inferredClass = (Class<V>) firstValue.getClass();
            return inferredClass;
        }
    }

    public static <V> In<V> in(String field, List<V> values, Class<V> valueClass) {
        return new In<>(field, values, valueClass);
    }

    public static <V> In<V> in(String field, List<V> values) {
        return in(field, values, null);
    }

    public static final class In<V> extends FieldSpecificationWithValuesList<V> {
        public In(String field, List<V> values, Class<V> valueClass) {
            super(field, values, valueClass);
        }

        public In(String field, List<V> values) {
            this(field, values, null);
        }
    }

    public static <V> Eq<V> eq(String field, V value, Class<V> valueClass) {
        return new Eq<>(field, value, valueClass);
    }

    public static <V> Eq<V> eq(String field, V value) {
        return eq(field, value, null);
    }

    public static final class Eq<V> extends FieldSpecificationWithSingleValue<V> {
        public Eq(String field, V value, Class<V> valueClass) {
            super(field, value, valueClass);
        }

        public Eq(String field, V value) {
            this(field, value, null);
        }
    }

    public static <V extends Comparable<V>> Lt<V> lt(String field, V value, Class<V> valueClass) {
        return new Lt<>(field, value, valueClass);
    }

    public static <V extends Comparable<V>> Lt<V> lt(String field, V value) {
        return lt(field, value, null);
    }

    public static final class Lt<V extends Comparable<V>> extends FieldSpecificationWithSingleValue<V> {
        public Lt(String field, V value, Class<V> valueClass) {
            super(field, value, valueClass);
        }

        public Lt(String field, V value) {
            this(field, value, null);
        }
    }


    /**
     * Conditions : And / Or
     */
    public abstract static class ConditionSpecification extends DomainSpecification {
        private final List<DomainSpecification> specifications;
        private ConditionSpecification(List<DomainSpecification> specifications) {
            if(specifications.stream().anyMatch(s -> s instanceof DomainSpecification.FullSpecification)) {
                throw new DomainSpecificationException("Full specifications cannot be nested");
            }
            this.specifications = specifications;
        }

        public List<DomainSpecification> getSpecifications() {
            return specifications;
        }

        @Override
        public boolean contains(Class<?> classToFind) {
            return super.contains(classToFind) ||  specifications.stream().anyMatch(s -> s.contains(classToFind));
        }
    }

    public static  Or or(List<DomainSpecification> criteriaList) {
        return new Or(criteriaList);
    }

    public static final class Or extends ConditionSpecification {
        public Or(List<DomainSpecification> criteriaList) {
            super(criteriaList);
        }
    }

    public static  And and(List<DomainSpecification> criteriaList) {
        return new And(criteriaList);
    }

    public static final class And extends ConditionSpecification {
        public And(List<DomainSpecification> criteriaList) {
            super(criteriaList);
        }
    }

    /**
     * Full specs : an object that encapsulates ALL specs needed to find something
     * Used for complex specs that cannot be expressed through existing (basic) DomainSpecification
     * The abstract FullSpecification is only used as a way to identify full specs
     *
     */
    public abstract static class FullSpecification extends DomainSpecification {
    }

    /** Specific Specification to allow passing a String query (aka search keyword) to the infra
     * As the domain cannot assume how the query can be executed or event on which "fields", we let
     * the infra handle it
      */
    public static class MatchQuerySpecification<V> extends DomainSpecification {

        private final Class<V> targetClass;

        private final String query;

        public MatchQuerySpecification(Class<V> targetClass, String query) {
            this.targetClass = targetClass;
            this.query = query;
        }

        public Class<V> getTargetClass() {
            return targetClass;
        }

        public String getQuery() {
            return query;
        }
    }


    /**
     * This specification is very specific, and should be understood and applied by the infra.
     * in this case this specification is used to query users who have not received any job follow-up reminders after some
     * "threshold" instant, which should be checked in infra, based on provided Instant and user's jobFollowUpReminderDelay
      */
    public static UserJobFollowUpReminderThreshold UserJobFollowUpReminderThreshold(Instant referenceInstant) {
        return new UserJobFollowUpReminderThreshold(referenceInstant);
    }
    public static final class UserJobFollowUpReminderThreshold extends FullSpecification {
        private final Instant referenceInstant;

        public UserJobFollowUpReminderThreshold(Instant referenceInstant) {
            this.referenceInstant = referenceInstant;
        }

        public Instant getReferenceInstant() {
            return referenceInstant;
        }
    }

    public static JobFollowUpToRemind JobFollowUpToRemind(Instant referenceInstant) {
        return new JobFollowUpToRemind(referenceInstant);
    }
    public static final class JobFollowUpToRemind extends FullSpecification {
        private final Instant referenceInstant;

        public JobFollowUpToRemind(Instant referenceInstant) {
            super();
            this.referenceInstant = referenceInstant;
            super.sortBy(sort("userId", SortDirection.ASC));
        }

        public Instant getReferenceInstant() {
            return referenceInstant;
        }
    }
}