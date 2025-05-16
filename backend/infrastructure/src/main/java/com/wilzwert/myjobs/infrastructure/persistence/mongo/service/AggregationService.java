package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class AggregationService {
    private final MongoTemplate mongoTemplate;

    private final DomainSpecificationConverter converter;

    public AggregationService(MongoTemplate mongoTemplate, DomainSpecificationConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    private Aggregation createAggregation(List<AggregationOperation> operationList, String sortString) {
        if(operationList.isEmpty()) {
            // in case we have no operation, we create an aggregation with either the sortString
            // which will default to default sort if sortString is null
            return Aggregation.newAggregation(getSortOperation(sortString));
        }
        Aggregation aggregation =  Aggregation.newAggregation(operationList);
        // in case we have operations, we only sort if a sortString is explicitly passed, as operationList
        // could already hold a sort operation
        if(sortString != null) {
            aggregation.getPipeline().add(getSortOperation(sortString));
        }
        return aggregation;
    }

    private <T> List<AggregationOperation> domainToOperations(DomainSpecification<T> specification) {
        return converter.convert(specification);
    }

    public <T> Aggregation createAggregation(DomainSpecification<T> specification, String sortString) {
        return createAggregation(domainToOperations(specification), sortString);
    }

    public <T> Aggregation createAggregation(JobId jobId, DomainSpecification<T> specification, String sortString) {
        List<AggregationOperation> operations = new ArrayList<>(List.of(Aggregation.match(Criteria.where("job_id").is(jobId.value()))));
        operations.addAll(domainToOperations(specification));
        return createAggregation(operations, sortString);
    }

    public <T> Aggregation createAggregation(UserId userId, DomainSpecification<T> specification, String sortString) {
        List<AggregationOperation> operations = new ArrayList<>(List.of(Aggregation.match(Criteria.where("user_id").is(userId.value()))));
        operations.addAll(domainToOperations(specification));
        return createAggregation(operations, sortString);
    }

    public <T> Aggregation createAggregationPaginated(UserId userId, DomainSpecification<T> specification, String sortString, int page, int size) {
        Aggregation aggregation = createAggregation(userId, specification, sortString);
        aggregation.getPipeline().add(Aggregation.skip((long) page * size));
        aggregation.getPipeline().add(Aggregation.limit(size));
        return aggregation;
    }

    public <T> List<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputClass) {
        AggregationResults<T> results = mongoTemplate.aggregate(aggregation, collectionName, outputClass);
        return results.getMappedResults();
    }

    public <T> Stream<T> stream(Aggregation aggregation, String collectionName, Class<T> outputClass) {
        return mongoTemplate.aggregateStream(aggregation, collectionName, outputClass);
    }

    /**
     *
     * @param aggregation the Aggregation we want the count for
     * @param collectionName the MongoDB collection name
     * @return the count as long
     */
    public long getAggregationCount(Aggregation aggregation, String collectionName) {
        List<AggregationOperation> stages = aggregation.getPipeline().getOperations().stream()
                .filter(MatchOperation.class::isInstance)
                .collect(Collectors.toList());
        stages.add(Aggregation.count().as("total"));
        AggregationResults<Document> countResults = mongoTemplate.aggregate(Aggregation.newAggregation(stages), collectionName, Document.class);
        Document resultDoc = countResults.getUniqueMappedResult();
        return resultDoc != null ? ((Number) resultDoc.get("total")).longValue() : 0L;
    }

    /**
     * Creates a SortOperation based on the sortString passed
     * In our case we know  all fields in mongodb are snake_case =/= camelCase
     * @param sortString the sort string e.g. "createdAt,desc"
     * @return a sort operation to be used for an Aggregation
     */
    public SortOperation getSortOperation(String sortString) {
        if (sortString == null || sortString.isEmpty()) {
            return Aggregation.sort(Sort.Direction.DESC, "created_at"); // default
        }
        String[] parts = sortString.split(",");
        String field = parts[0].replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Aggregation.sort(direction, field);
    }
}