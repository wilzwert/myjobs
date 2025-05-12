package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.shared.querying.DomainQueryingOperation;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class AggregationService {
    private final MongoTemplate mongoTemplate;

    private final DomainQueryingConverter converter;

    public AggregationService(MongoTemplate mongoTemplate, DomainQueryingConverter converter) {
        this.mongoTemplate = mongoTemplate;
        this.converter = converter;
    }

    private Aggregation createAndConfigureAggregationWithOperations(List<AggregationOperation> operationList, String sortString) {
        Aggregation aggregation =  Aggregation.newAggregation(operationList);
        aggregation.getPipeline().add(getSortOperation(sortString));
        return aggregation;
    }

    private Aggregation configureAggregation(Aggregation aggregation, List<AggregationOperation> operationList, String sortString) {
        for(AggregationOperation operation : operationList) {
            aggregation.getPipeline().add(operation);
        }
        aggregation.getPipeline().add(getSortOperation(sortString));
        return aggregation;
    }

    private Aggregation createAggregationWithOperations(List<AggregationOperation> operationList, String sortString) {
        return createAndConfigureAggregationWithOperations(operationList, sortString);
    }

    private List<AggregationOperation> domainToOperations(List<DomainQueryingOperation> operations) {
        return converter.convert(operations);
    }


    public Aggregation createAggregation(List<DomainQueryingOperation> operations, String sortString) {
        return createAggregationWithOperations(domainToOperations(operations), sortString);
    }

    public Aggregation createAggregation(Job job, List<DomainQueryingOperation> operations, String sortString) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("job_id").is(job.getId().value())));
        return configureAggregation(aggregation, domainToOperations(operations), sortString);
    }

    public Aggregation createAggregation(User user, List<DomainQueryingOperation> operations, String sortString) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("user_id").is(user.getId().value())));
        return configureAggregation(aggregation, domainToOperations(operations), sortString);
    }

    public Aggregation createAggregationPaginated(User user, List<DomainQueryingOperation> operations, String sortString, int page, int size) {
        Aggregation aggregation = createAggregation(user, operations, sortString);
        aggregation.getPipeline().add(Aggregation.skip((long) page * size));
        aggregation.getPipeline().add(Aggregation.limit(size));
        return aggregation;
    }

    public <T> List<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputClass) {
        AggregationResults<T> results = mongoTemplate.aggregate(aggregation, collectionName, outputClass);
        return results.getMappedResults();
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
     *
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