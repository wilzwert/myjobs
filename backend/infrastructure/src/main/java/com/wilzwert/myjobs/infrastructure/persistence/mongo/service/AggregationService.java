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
            return Aggregation.newAggregation(getSortOperation(sortString));
        }
        Aggregation aggregation =  Aggregation.newAggregation(operationList);
        aggregation.getPipeline().add(getSortOperation(sortString));
        return aggregation;
    }

    private <T> List<AggregationOperation> domainToOperations(DomainSpecification<T> specifications) {
        return converter.convert(specifications);
    }

    public <T> Aggregation createAggregation(DomainSpecification<T> specifications, String sortString) {
        return createAggregation(domainToOperations(specifications), sortString);
    }

    public <T> Aggregation createAggregation(JobId jobId, DomainSpecification<T> specifications, String sortString) {
        List<AggregationOperation> operations = new ArrayList<>(List.of(Aggregation.match(Criteria.where("job_id").is(jobId.value()))));
        operations.addAll(domainToOperations(specifications));
        return createAggregation(operations, sortString);
    }

    public <T> Aggregation createAggregation(UserId userId, DomainSpecification<T> specifications, String sortString) {
        System.out.println("adding user_id operation");
        List<AggregationOperation> operations = new ArrayList<>(List.of(Aggregation.match(Criteria.where("user_id").is(userId.value()))));
        operations.addAll(domainToOperations(specifications));
        return createAggregation(operations, sortString);
    }

    public <T> Aggregation createAggregationPaginated(UserId userId, DomainSpecification<T> specifications, String sortString, int page, int size) {
        Aggregation aggregation = createAggregation(userId, specifications, sortString);
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