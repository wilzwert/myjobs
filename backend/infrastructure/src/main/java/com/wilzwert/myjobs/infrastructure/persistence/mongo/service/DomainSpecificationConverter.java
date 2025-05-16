package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriterionException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/05/2025
 * Time:11:18
 * This converter should convert DomainSpecification types to Aggregation.Operation types
 */

@Service
public class DomainSpecificationConverter {

    public <T> List<AggregationOperation> convert(DomainSpecification<T> specifications) {
        if (specifications == null) {
            return Collections.emptyList();
        }
        return domainSpecificationToAggregationOperation(specifications);
    }

    /**
     *
     * @param domainSpecification a query criterion received from the domain
     * @return a MongoDb Criteria
     */
    public <T> Criteria domainCriterionToCriteria(DomainSpecification<T> domainSpecification) {
        switch (domainSpecification) {
            case null -> {
                System.out.println("criteria is null because domainSpecification is null");
                return null;
            }
            case DomainSpecification.Eq<T, ?> c -> {
                System.out.println("criteria is EQ for " + c.getField());
                return Criteria.where(c.getField()).is(c.getValue());
            }
            case DomainSpecification.In<T, ?> c -> {
                System.out.println("criteria is In for " + c.getField());
                return Criteria.where(c.getField()).in(c.getValues());
            }
            case DomainSpecification.Lt<T, ?> c -> {
                System.out.println("criteria is Lt for " + c.getField());
                return Criteria.where(c.getField()).lt(c.getValue());
            }
            case DomainSpecification.Or<T> c -> {
                System.out.println("criteria is Or");
                return new Criteria().orOperator(c.getSpecifications().stream().map(this::domainCriterionToCriteria).toList());
            }
            case DomainSpecification.And<T> c -> {
                System.out.println("criteria is And");
                return new Criteria().andOperator(c.getSpecifications().stream().map(this::domainCriterionToCriteria).toList());
            }
            default -> {
            }
        }

        throw new UnsupportedDomainCriterionException(domainSpecification.getClass().getName());
    }

    /**
     *
     * @param domainSpecification a criterion received from the domain
     * @return a List or AggregationOperation (MatchOperation) to be used in an Aggregation pipeline
     */
    public <T> List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification<T> domainSpecification) {
        System.out.println("adding "+domainSpecification.getClass().getName());

        if(domainSpecification instanceof DomainSpecification.Sort<T> sort) {
        return List.of(Aggregation.sort(sort.getSortDirection().equals(DomainSpecification.SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC, sort.getFieldName()));
        }

        if(domainSpecification instanceof DomainSpecification.FullSpecification<T> fullSpecification) {
            return domainSpecificationToAggregationOperation(fullSpecification);
        }

        return List.of(Aggregation.match(domainCriterionToCriteria(domainSpecification)));
    }

    private <T> List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification.FullSpecification<T> fullDomainSpecification) {
        List<AggregationOperation> operations = null;
        if(fullDomainSpecification instanceof DomainSpecification.UserJobFollowUpReminderThreshold<T> userJobFollowUpReminderThreshold) {
            operations = domainSpecificationToAggregationOperation(userJobFollowUpReminderThreshold);
        }

        if(fullDomainSpecification instanceof DomainSpecification.JobFollowUpToRemind jobFollowUpToRemind ) {
            operations = domainSpecificationToAggregationOperation(jobFollowUpToRemind);
        }

        if(operations == null) {
            throw new UnsupportedDomainCriterionException(fullDomainSpecification.getClass().getName());
        }

        operations.addAll(fullDomainSpecification.getNested().stream().map(this::domainSpecificationToAggregationOperation).flatMap(Collection::stream).toList());
        return operations;

    }

    /**
     *  This converts a specific Criterion  : users who have not received any job follow-up reminders after
     *  a threshold instant calculated by Mongo, based on provided Instant and user's jobFollowUpReminderDelay
     * @param domainSpecification the Specification received from the domain
     * @return a List or AggregationOperation to be used in an Aggregation pipeline
     */
    public <T> List<AggregationOperation> domainSpecificationToAggregationOperation(DomainSpecification.UserJobFollowUpReminderThreshold<T> domainSpecification) {
        return List.of(
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
        );
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


        aggregationOperations.add(Aggregation.addFields()
                .addFieldWithValue("updated_at_millis", ConvertOperators.ToLong.toLong("$updated_at")).build());

        // filter by updatedAt (or statusUpdatedAt ?) < thresholdDate
        aggregationOperations.add(Aggregation.match(
                Criteria.expr(
                    ComparisonOperators.Lte.valueOf("updated_at_millis").lessThanEqualTo("job_follow_up_reminder_threshold"))
        ));
        // filter lastReminderSentAt null or < thresholdDate
        /*aggregationOperations.add(Aggregation.match(
                new Criteria().orOperator(
                    Criteria.where("follow_up_reminder_sent_at").isNull(),
                    Criteria.where("follow_up_reminder_sent_at").lte("job_follow_up_reminder_threshold")
                )
        ));*/
        return aggregationOperations;
    }
}
