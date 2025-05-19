package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.shared.specification.DomainSpecification;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

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

    private Aggregation createAggregation(List<AggregationOperation> operationList) {
        if(operationList.isEmpty()) {
            throw new IllegalArgumentException("Cannot create Aggregation : Operation list is empty");
        }

        return Aggregation.newAggregation(operationList);
    }

    private List<AggregationOperation> domainToOperations(DomainSpecification specification) {
        return converter.convert(specification);
    }

    public Aggregation createAggregation(DomainSpecification specification) {
        return createAggregation(domainToOperations(specification));
    }

    public Aggregation createAggregationPaginated(DomainSpecification specification, int page, int size) {
        Aggregation aggregation = createAggregation(domainToOperations(specification));
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
}