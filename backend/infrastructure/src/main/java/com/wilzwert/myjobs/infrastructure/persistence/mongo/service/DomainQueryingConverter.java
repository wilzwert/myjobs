package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;


import com.wilzwert.myjobs.core.domain.shared.querying.DomainQueryingOperation;
import com.wilzwert.myjobs.core.domain.shared.querying.criteria.DomainQueryingCriterion;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriterionException;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/05/2025
 * Time:11:18
 */

@Service
public class DomainQueryingConverter  {

    public List<AggregationOperation> convert(List<? extends DomainQueryingOperation> operations) {
        List<AggregationOperation> convertedOperations = new ArrayList<>();
        for (DomainQueryingOperation operation : operations) {
            // only criterion is supported at the moment
            if(operation instanceof DomainQueryingCriterion c) {
                convertedOperations.add(domainOperationToAggregationOperation(c));
            }
        }
        return convertedOperations;
    }

    /**
     *
     * @param domainQueryingCriterion a criteria received from the domain
     * @return a Criteria
     */
    public Criteria domainCriterionToCriteria(DomainQueryingCriterion domainQueryingCriterion) {
        if(domainQueryingCriterion instanceof DomainQueryingCriterion.Eq<?> c) {
            return Criteria.where(c.getField()).is(c.getValue());
        }

        if(domainQueryingCriterion instanceof DomainQueryingCriterion.In<?> c) {
            return Criteria.where(c.getField()).in(c.getValues());
        }

        if(domainQueryingCriterion instanceof DomainQueryingCriterion.Lt<?> c) {
            return Criteria.where(c.getField()).lt(c.getValue());
        }

        if(domainQueryingCriterion instanceof DomainQueryingCriterion.Or c) {
            return new Criteria().orOperator(c.getCriteriaList().stream().map(this::domainCriterionToCriteria).toList());
        }

        throw new UnsupportedDomainCriterionException(domainQueryingCriterion.getClass().getName());
    }

    /**
     *
     * @param domainQueryingCriterion a criterion received from the domain
     * @return a MatchOperation to be used in an Aggregation pipeline
     */
    public MatchOperation domainOperationToAggregationOperation(DomainQueryingCriterion domainQueryingCriterion) {
        return Aggregation.match(domainCriterionToCriteria(domainQueryingCriterion));
    }

    /**
     *
     * @param domainQueryingCriterion a criteria received from the domain
     * @return a MatchOperation to be used in an Aggregation pipeline
     */
    public AddFieldsOperation domainOperationToAggregationOperation(DomainQueryingCriterion.UserJobFollowUpReminderThreshold domainQueryingCriterion) {
        return Aggregation.addFields()
                .addFieldWithValue(
                        "jobFollowUpReminderThreshold",
                        ArithmeticOperators.Subtract.valueOf(domainQueryingCriterion.getReferenceInstant().toEpochMilli()).subtract(
                                ArithmeticOperators.Multiply.valueOf("user.jobFollowUpReminderDays")
                                        .multiplyBy(86400000)
                        )
                ).build();
    }
}
