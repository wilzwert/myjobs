package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.model.activity.ActivityId;
import com.wilzwert.myjobs.core.domain.model.activity.ActivityType;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.job.JobRating;
import com.wilzwert.myjobs.core.domain.model.job.JobStatus;
import com.wilzwert.myjobs.core.domain.model.job.command.UpdateJobFieldCommand;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserUpdatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Wilhelm Zwertvaegher
 * Date:17/06/2025
 * Time:10:57
 */
public class KafkaKafkaIntegrationEventTopicResolverTest {

    private KafkaIntegrationEventTopicResolver resolver;

    static Stream<Arguments> provideEventAndExpectedTopic() {
        return Stream.of(
                Arguments.of(new JobCreatedEvent(IntegrationEventId.generate(), JobId.generate()), "jobs"),
                Arguments.of(new JobUpdatedEvent(IntegrationEventId.generate(), JobId.generate()), "jobs"),
                Arguments.of(new JobDeletedEvent(IntegrationEventId.generate(), JobId.generate()), "jobs"),

                Arguments.of(new JobFieldUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), UpdateJobFieldCommand.Field.COMMENT), "jobs"),

                Arguments.of(new JobStatusUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), JobStatus.PENDING), "jobs"),

                Arguments.of(new JobRatingUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), JobRating.of(4)), "jobs"),

                Arguments.of(new ActivityCreatedEvent(IntegrationEventId.generate(), JobId.generate(), ActivityId.generate(), ActivityType.EMAIL), "jobs"),
                Arguments.of(new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), JobId.generate(), ActivityId.generate(), ActivityType.EMAIL), "jobs"),

                Arguments.of(new AttachmentCreatedEvent(IntegrationEventId.generate(), JobId.generate(), AttachmentId.generate()), "jobs"),
                Arguments.of(new AttachmentDeletedEvent(IntegrationEventId.generate(), JobId.generate(), AttachmentId.generate()), "jobs"),

                Arguments.of(new UserCreatedEvent(IntegrationEventId.generate(), UserId.generate()), "users"),
                Arguments.of(new UserUpdatedEvent(IntegrationEventId.generate(), UserId.generate()), "users"),
                Arguments.of(new UserDeletedEvent(IntegrationEventId.generate(), UserId.generate()), "users")
        );
    }

    @BeforeEach
    public void setUp() {
        KafkaProperties kafkaProperties = mock(KafkaProperties.class);
        when(kafkaProperties.getTopicPrefix()).thenReturn("");
        resolver = new KafkaIntegrationEventTopicResolver(kafkaProperties);
    }

    @ParameterizedTest
    @MethodSource("provideEventAndExpectedTopic")
    void shouldMapEventsToTopics(IntegrationEvent event, String expectedTopic) {
        assertThat(resolver.resolve(event)).isEqualTo(expectedTopic);
    }

    @Test
    void shouldMapAllIntegrationEventSubclasses() {
        Class<?>[] classes = IntegrationEvent.class.getPermittedSubclasses();

        // check that the provider method for the 'shouldMapEventsToTopics' test is accurate
        assertThat(provideEventAndExpectedTopic().count()).isEqualTo(classes.length);

        // Ensure all permitted subclasses of IntegrationEvent are handled by the resolver
        Arrays.stream(classes)
                .toList()
                .forEach
                    (permitted -> assertDoesNotThrow(() -> resolver.resolve(permitted.asSubclass(IntegrationEvent.class)))
                );
    }
}