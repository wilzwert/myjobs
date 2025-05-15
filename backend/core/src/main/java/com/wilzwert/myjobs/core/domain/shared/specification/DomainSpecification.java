package com.wilzwert.myjobs.core.domain.shared.specification;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainSpecificationException;

import java.time.Instant;
import java.util.Collections;
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
 * jobService.find(DomainSpecification.And(List.of(
 *  DomainSpecification.Eq("status", JobStatus.PENDING),*
 *  DomainSpecification.Lt("createdAt", Instant.now() - 7 * 86_400_000),
 *  DomainSpecification.Lt("updatedAt", Instant.now() - 3 * 86_400_000)
 *  ))
 * instead of adding a findByStatusPendingAndCreatedMoreThan7DaysAgoAndUpdatedMoreThan3DaysAgo method to our JobService interface
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
public abstract class DomainSpecification<T> {
    protected DomainSpecification() {}

    /**
     * In some cases, infra may have to check if a certain specification exists in the current hierarchy
     * to change implementation strategy
     * @param classToFind the class to find in the current specification hierarchy
     * @return true if class found
     */
    public boolean contains(Class<?> classToFind) {
        return classToFind.equals(getClass());
    }




    public enum SortDirection {
        ASC, DESC
    }

    public static <T> Sort<T> Sort(String fieldName, SortDirection sortDirection) {
        return new Sort<>(fieldName, sortDirection);
    }

    public static class Sort<T> extends DomainSpecification<T> {
        private final String fieldName;
        private final SortDirection sortDirection;

        Sort(String fieldName, SortDirection sortDirection) {
            this.fieldName = fieldName;
            this.sortDirection = sortDirection;
        }

        public String getFieldName() {
            return fieldName;
        }

        public SortDirection getSortDirection() {
            return sortDirection;
        }
    }

    private abstract static class FieldSpecification<T> extends DomainSpecification<T> {
        private final String field;
        private FieldSpecification(String field) {
            this.field = field;
        }
        public String getField() {
            return field;
        }
    }

    private abstract static class ConditionSpecification<T> extends DomainSpecification<T> {
        private final List<DomainSpecification<T>> specifications;
        private ConditionSpecification(List<DomainSpecification<T>> specifications) {
            if(specifications.stream().anyMatch(s -> s instanceof DomainSpecification.FullSpecification<?>)) {
                throw new DomainSpecificationException("Full specifications cannot be nested");
            }
            this.specifications = specifications;
        }

        public List<DomainSpecification<T>> getSpecifications() {
            return specifications;
        }

        @Override
        public boolean contains(Class<?> classToFind) {
            return super.contains(classToFind) ||  specifications.stream().anyMatch(specifications -> specifications.contains(classToFind));
        }
    }

    public static <T, V> In<T, V> In(String field, List<V> values) {
        return new In<>(field, values);
    }

    public static final class In<T, V> extends FieldSpecification<T> {

        private final  List<V> values;

        public In(String field, List<V> values) {
            super(field);
            this.values = values;
        }

        public List<V> getValues() {
            return values;
        }
    }

    public static <T, V> Eq<T, V> Eq(String field, V value) {
        return new Eq<>(field, value);
    }

    public static final class Eq<T, V> extends FieldSpecification<T> {
        private final V value;
        public Eq(String field, V value) {
            super(field);
            this.value = value;
        }

        public V getValue() {
            return value;
        }
    }

    public static <T, V extends Comparable<V>> Lt<T, V> Lt(String field, V value) {
        return new Lt<>(field, value);
    }

    public static final class Lt<T, V extends Comparable<V>> extends FieldSpecification<T> {
        private final V value;

        public Lt(String field, V value) {
            super(field);
            this.value = value;
        }

        public V getValue() {
            return value;
        }
    }

    public static <T> Or<T> Or(List<DomainSpecification<T>> criteriaList) {
        return new Or<>(criteriaList);
    }

    public static final class Or<T> extends ConditionSpecification<T> {
        public Or(List<DomainSpecification<T>> criteriaList) {
            super(criteriaList);
        }
    }

    public static <T> And<T> And(List<DomainSpecification<T>> criteriaList) {
        return new And<>(criteriaList);
    }

    public static final class And<T> extends ConditionSpecification<T> {
        public And(List<DomainSpecification<T>> criteriaList) {
            super(criteriaList);
        }
    }

    public abstract static class FullSpecification<T> extends DomainSpecification<T> {
        private final List<DomainSpecification<T>> nested;

        public FullSpecification(List<DomainSpecification<T>> nested) {
            this.nested = nested;
        }

        public FullSpecification() {
            this.nested = Collections.emptyList();
        }

        public List<DomainSpecification<T>> getNested() {
            return nested;
        }
    }


    /**
     * This specification is very specific, and should be understood and applied by the infra.
     * in this case this specification is used to query users who have not received any job follow-up reminders after some
     * "threshold" instant, which should be checked in infra, based on provided Instant and user's jobFollowUpReminderDelay
      */
    public static <T> UserJobFollowUpReminderThreshold<T> UserJobFollowUpReminderThreshold(Instant referenceInstant) {
        return new UserJobFollowUpReminderThreshold<>(referenceInstant);
    }
    public static final class UserJobFollowUpReminderThreshold<T> extends FullSpecification<T> {
        private final Instant referenceInstant;

        public UserJobFollowUpReminderThreshold(Instant referenceInstant) {
            this.referenceInstant = referenceInstant;
        }

        public Instant getReferenceInstant() {
            return referenceInstant;
        }
    }

    /**
     * This Specification is used to query a list of Job based on these criteria :
     * - jobs are active (ie status IN JobStatus.activeStatuses())
     * - have ever been reminded OR
     *   have not been reminded in the last user.jobFollowUpReminderDelay before provided referenceInstant
     * As this would be nearly impossible to effectively model it using DomainSpecification
     * (especially because we would be assuming how the infra persistence layer works (joins in Sql based DBMS, collections in NoSql...)),
     * we kindly ask (and trust) the infra to handle it
     */
    public static JobFollowUpToRemind JobFollowUpToRemind(Instant referenceInstant) {
        return new JobFollowUpToRemind(referenceInstant);
    }
    public static final class JobFollowUpToRemind extends FullSpecification<Job> {
        private final Instant referenceInstant;

        public JobFollowUpToRemind(Instant referenceInstant) {
            super(List.of(Sort("userId", SortDirection.ASC)));
            this.referenceInstant = referenceInstant;
        }

        public Instant getReferenceInstant() {
            return referenceInstant;
        }
    }
}