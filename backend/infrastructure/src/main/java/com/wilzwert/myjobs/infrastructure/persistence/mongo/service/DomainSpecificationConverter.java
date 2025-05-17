package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriterionException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/05/2025
 * Time:11:18
 * This converter should convert DomainSpecification types to Aggregation.Operation types
 */

@Service
public class DomainSpecificationConverter {

    private final static Map<Class<?>, Function<DomainSpecification.FieldSpecificationWithValuesList<?>, List<?>>> valuesClassMap = Map.of(
        UserId.class, spec -> spec.getValues().stream().map(u -> ((UserId) u).value()).toList(),
        JobId.class, spec -> spec.getValues().stream().map(u -> ((JobId) u).value()).toList()
    );

    private final static Map<Class<?>, Function<DomainSpecification.FieldSpecificationWithSingleValue<?>, ?>> valueClassMap = Map.of(
            UserId.class, spec -> ((UserId) spec.getValue()).value(),
            JobId.class, spec -> ((JobId) spec.getValue()).value()
    );

    public List<AggregationOperation> convert(DomainSpecification specifications) {
        if (specifications == null) {
            return Collections.emptyList();
        }
        return domainSpecificationToAggregationOperation(specifications);
    }

    public String convertField(String field) {
        if("id".equals(field)) {
            return "_id";
        }
        return field.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private <S extends DomainSpecification.FieldSpecificationWithSingleValue<V>, V> Object convertValue(S spec) {
        Function<DomainSpecification.FieldSpecificationWithSingleValue<?>, ?> function = valueClassMap.get(spec.getValueClass());
        if(function == null) {
            return spec.getValue();
        }
        return function.apply(spec);

    }

    private <S extends DomainSpecification.FieldSpecificationWithValuesList<V>, V> List<?> convertValues(S spec) {
        Function<DomainSpecification.FieldSpecificationWithValuesList<?>, List<?>> function = valuesClassMap.get(spec.getValueClass());
        if(function == null) {
            return spec.getValues();
        }
        return function.apply(spec);
    }

    /**
     *
     * @param domainSpecification a query criterion received from the domain
     * @return a MongoDb Criteria
     */
    public Criteria domainCriterionToCriteria(DomainSpecification domainSpecification) {
        switch (domainSpecification) {
            case null -> {
                System.out.println("criteria is null because domainSpecification is null");
                return null;
            }
            case DomainSpecification.Eq<?> c -> {
                System.out.println("criteria is EQ for " + c.getField());
                return Criteria.where(convertField(c.getField())).is(convertValue(c));
            }
            case DomainSpecification.In<?> c -> {
                System.out.println("criteria is In for " + c.getField());
                System.out.println(c.getValues());
                return Criteria.where(convertField(c.getField())).in(convertValues(c));
            }
            case DomainSpecification.Lt<?> c -> {
                System.out.println("criteria is Lt for " + c.getField());
                return Criteria.where(convertField(c.getField())).lt(convertValue(c));
            }
            case DomainSpecification.Or c -> {
                System.out.println("criteria is Or");
                return new Criteria().orOperator(c.getSpecifications().stream().map(this::domainCriterionToCriteria).toList());
            }
            case DomainSpecification.And c -> {
                System.out.println("criteria is And");
                return new Criteria().andOperator(c.getSpecifications().stream().map(this::domainCriterionToCriteria).toList());
            }
            default -> throw new UnsupportedDomainCriterionException(domainSpecification.getClass().getName());
        }
    }

    private AggregationOperation domainSpecificationSortToAggregationOperation(DomainSpecification.Sort sort) {
        System.out.println("converting sort");
        return Aggregation.sort(sort.getSortDirection().equals(DomainSpecification.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC, convertField(sort.getFieldName()));
    }

    /**
     *
     * @param domainSpecification a spec received from the domain
     * @return a List or AggregationOperation (match, lookup, sort...) to be used in an Aggregation pipeline
     */
    public  List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification domainSpecification) {
        if(domainSpecification instanceof DomainSpecification.Sort sort) {
            return List.of(domainSpecificationSortToAggregationOperation(sort));
        }

        List<AggregationOperation> result;
        if(domainSpecification instanceof DomainSpecification.FullSpecification fullSpecification) {
            result = domainSpecificationToAggregationOperation(fullSpecification);
        }
        else {
            result = new ArrayList<>(List.of(Aggregation.match(domainCriterionToCriteria(domainSpecification))));
        }

        result.addAll(domainSpecification.getSort().stream().map(this::domainSpecificationSortToAggregationOperation).toList());
        return result;
    }

    private List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification.FullSpecification fullDomainSpecification) {
        if(fullDomainSpecification instanceof DomainSpecification.UserJobFollowUpReminderThreshold userJobFollowUpReminderThreshold) {
            return domainSpecificationToAggregationOperation(userJobFollowUpReminderThreshold);
        }

        if(fullDomainSpecification instanceof DomainSpecification.JobFollowUpToRemind jobFollowUpToRemind ) {
            return domainSpecificationToAggregationOperation(jobFollowUpToRemind);
        }

        throw new UnsupportedDomainCriterionException(fullDomainSpecification.getClass().getName());

    }

    /**
     *  This converts a specific Criterion  : users who have not received any job follow-up reminders after
     *  a threshold instant calculated by Mongo, based on provided Instant and user's jobFollowUpReminderDelay
     * @param domainSpecification the Specification received from the domain
     * @return a List or AggregationOperation to be used in an Aggregation pipeline
     */
    public List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification.UserJobFollowUpReminderThreshold domainSpecification) {
    return new ArrayList<>(List.of(
                Aggregation.addFields()
                    .addFieldWithValue(
                            "jobFollowUpReminderThreshold",
                            ArithmeticOperators.Subtract.valueOf(domainSpecification.getReferenceInstant().toEpochMilli()).subtract(
                                    ArithmeticOperators.Multiply.valueOf(
                                            ConditionalOperators.ifNull("user.jobFollowUpReminderDays").then(0)
                                    )
                                    .multiplyBy(86400000)
                            )
                    ).build(),
                Aggregation.match(Criteria.where("jobFollowUpReminderSentAt").lt("jobFollowUpReminderThreshold"))
        ));
    }

    /**
     *  This converts a specific Criterion  : active jobs who have not been reminded after
     *  a threshold instant calculated by Mongo, based on provided Instant and user's jobFollowUpReminderDelay
     * @param domainSpecification the Specification received from the domain
     * @return a List or AggregationOperation to be used in an Aggregation pipeline
     */
    public List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification.JobFollowUpToRemind domainSpecification) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        // filter by status
        aggregationOperations.add(Aggregation.match(Criteria.where("status").in(JobStatus.activeStatuses())));

        // lookup users collection
        aggregationOperations.add(Aggregation.lookup().from("users").localField("user_id").foreignField("_id").as("user"));
        // unwind users
        aggregationOperations.add(Aggregation.unwind("user"));
        // filter users with no job_follow_up_reminder_days
        aggregationOperations.add(Aggregation.match(Criteria.where("user.job_follow_up_reminder_days").exists(true).ne(null)));

        // compute 'thresholdDate' based on provided instant and user.jobFollowUpReminderDelay
        aggregationOperations.add( Aggregation.addFields()
                .addFieldWithValue(
                        "job_follow_up_reminder_threshold",
                        ArithmeticOperators.Subtract.valueOf(domainSpecification.getReferenceInstant().toEpochMilli()).subtract(
                                ArithmeticOperators.Multiply.valueOf("user.job_follow_up_reminder_days")
                                        .multiplyBy(86400000)
                        )
                ).build());

        // convert job's updatedAt Instant to a long (in millis)
        aggregationOperations.add(Aggregation.addFields()
                .addFieldWithValue("updated_at_millis", ConvertOperators.ToLong.toLong("$updated_at")).build());

        // filter by updatedAt (or statusUpdatedAt ?) < threshold
        aggregationOperations.add(Aggregation.match(
                Criteria.expr(
                    ComparisonOperators.Lte.valueOf("updated_at_millis").lessThanEqualTo("job_follow_up_reminder_threshold"))
        ));

        // convert job's followUpReminderSentAt Instant to a long (in millis)
        aggregationOperations.add(
            Aggregation.addFields()
                .addFieldWithValue(
                    "job_follow_up_reminder_sent_at_millis",
                        ConvertOperators.ToLong.toLong(
                            ConditionalOperators.ifNull("follow_up_reminder_sent_at").then(0)
                        )
            ).build()
        );

        // filter lastReminderSentAt < threshold to avoid multiple reminders
        aggregationOperations.add(Aggregation.match(
                Criteria.expr(
                        ComparisonOperators.Lte.valueOf("job_follow_up_reminder_sent_at_millis").lessThanEqualTo("job_follow_up_reminder_threshold"))
        ));

        return aggregationOperations;
    }
}
