package com.wilzwert.myjobs.infrastructure.persistence.mongo.service;

import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.shared.criteria.DomainCriteria;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.exception.UnsupportedDomainCriteriaException;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private AggregationService service;
    private final User testUser = User.builder()
            .id(UserId.generate())
            .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .username("johndoe")
                .jobFollowUpReminderDays(30).build();

    @BeforeEach
    void setup() {
        mongoTemplate = mock(MongoTemplate.class);
        service = new AggregationService(mongoTemplate);
    }

    @Test
    void shouldBuildPipelineWithMatchSortSkipLimit() {
        List<DomainCriteria> criteria = List.of(
                new DomainCriteria.In<>("status", List.of("ACTIVE", "INTERVIEW")),
                new DomainCriteria.Lt<>("status_updated_at", Instant.parse("2023-01-01T00:00:00Z"))
        );
        Aggregation aggregation = service.createAggregationPaginated(testUser, criteria, "status,asc", 1, 20);

        List<AggregationOperation> operations = aggregation.getPipeline().getOperations();

        assertThat(operations).hasSize(6);
        assertThat(operations.get(0)).isInstanceOf(MatchOperation.class); // match user_id
        assertThat(operations.get(1)).isInstanceOf(MatchOperation.class); // match In
        assertThat(operations.get(2)).isInstanceOf(MatchOperation.class); // match Lt
        assertThat(operations.get(3)).isInstanceOf(SortOperation.class);
        assertThat(operations.get(4)).isInstanceOf(SkipOperation.class);
        assertThat(operations.get(5)).isInstanceOf(LimitOperation.class);
    }

    @Test
    void whenSortPassed_thenShouldParseFieldAndDirectionCorrectly() {
        Aggregation aggregation = service.createAggregationPaginated(testUser, List.of(), "statusUpdatedAt,desc", 0, 10);

        List<Document> pipeline = aggregation.toPipeline(Aggregation.DEFAULT_CONTEXT);

        // On recherche l'étape $sort dans la pipeline
        Document sortStage = pipeline.stream()
                .filter(doc -> doc.containsKey("$sort"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No $sort stage found"));

        Document sortDoc = (Document) sortStage.get("$sort");

        assertThat(sortDoc).containsEntry("status_updated_at", -1); // -1 = DESC
    }

    @Test
    void whenNoSortPassed_thenShouldParseFieldAndDirectionCorrectly() {
        Aggregation aggregation = service.createAggregationPaginated(testUser, List.of(), null, 0, 10);

        List<Document> pipeline = aggregation.toPipeline(Aggregation.DEFAULT_CONTEXT);

        Document sortStage = pipeline.stream()
                .filter(doc -> doc.containsKey("$sort"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No $sort stage found"));

        Document sortDoc = (Document) sortStage.get("$sort");

        assertThat(sortDoc).containsEntry("created_at", -1); // -1 = DESC
    }

    @Test
    void whenNoResult_thenAggregationCountShouldReturnZero() {
        Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("field").is("value")));
        AggregationResults<Document> mockResult = new AggregationResults<>(List.of(), new Document());

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("jobs"), eq(Document.class))).thenReturn(mockResult);

        long count = service.getAggregationCount(agg, "jobs");
        assertThat(count).isZero();
    }

    @Test
    void shouldReturnCorrectCount() {
        Document countDoc = new Document("total", 42L);
        AggregationResults<Document> result = new AggregationResults<>(List.of(countDoc), new Document());

        when(mongoTemplate.aggregate(any(Aggregation.class), eq("jobs"), eq(Document.class))).thenReturn(result);

        Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("x").is("y")));
        long count = service.getAggregationCount(agg, "jobs");

        assertThat(count).isEqualTo(42L);
    }

    @Test
    void whenDomainCriteriaNotSupported_thenShouldThrowException() {
        class UnsupportedCriteria extends DomainCriteria {
            UnsupportedCriteria() {
                super();
            }
        }

        var unsupported = new UnsupportedCriteria();

        assertThrows(
            UnsupportedDomainCriteriaException.class,
            () -> service.domainCriteriaToMatchOperation(unsupported)
        );
    }
}