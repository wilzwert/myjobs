package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.shared.criteria.DomainCriteria;
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

    public AggregationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Aggregation createAggregation(User user, List<DomainCriteria> criteriaList, String sortString) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("user_id").is(user.getId().value())));

        List<MatchOperation> operations = criteriaList.stream().map(this::domainCriteriaToMatchOperation).toList();
        for(MatchOperation operation : operations) {
            aggregation.getPipeline().add(operation);
        }
        aggregation.getPipeline().add(getSortOperation(sortString));
        return aggregation;
    }

    public Aggregation createAggregationPaginated(User user, List<DomainCriteria> criteriaList, String sortString, int page, int size) {
        Aggregation aggregation = createAggregation(user, criteriaList, sortString);
        aggregation.getPipeline().add(Aggregation.skip((long) page * size));
        aggregation.getPipeline().add(Aggregation.limit(size));
        return aggregation;
    }

    public long getAggregationCount(Aggregation aggregation) {
        List<AggregationOperation> stages = aggregation.getPipeline().getOperations().stream()
                .filter(o -> o instanceof MatchOperation)
                .collect(Collectors.toList());
        stages.add(Aggregation.count().as("total"));
        AggregationResults<Document> countResults = mongoTemplate.aggregate(Aggregation.newAggregation(stages), "jobs", Document.class);
        Document resultDoc = countResults.getUniqueMappedResult();
        return resultDoc != null ? ((Number) resultDoc.get("total")).longValue() : 0L;
    }

    private MatchOperation domainCriteriaToMatchOperation(DomainCriteria domainCriteria) {
        if(domainCriteria instanceof DomainCriteria.Eq<?>) {
            return Aggregation.match(Criteria.where(domainCriteria.getField()).is(((DomainCriteria.Eq<?>) domainCriteria).getValue()));
        }

        if(domainCriteria instanceof DomainCriteria.In<?>) {
            return Aggregation.match(Criteria.where(domainCriteria.getField()).in(((DomainCriteria.In<?>) domainCriteria).getValues()));
        }

        if(domainCriteria instanceof DomainCriteria.Lt<?>) {
            return Aggregation.match(Criteria.where(domainCriteria.getField()).lt(((DomainCriteria.Lt<?>) domainCriteria).getValue()));
        }

        throw new UnsupportedOperationException(domainCriteria.getClass().getName());

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
