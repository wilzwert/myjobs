package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.EventStatus;
import com.wilzwert.myjobs.infrastructure.persistence.mongo.entity.MongoIntegrationEvent;
import com.wilzwert.myjobs.infrastructure.serialization.IntegrationEventSerializationHandler;
import com.wilzwert.myjobs.infrastructure.serialization.exception.DeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntegrationEventSerializationHandlerIT extends AbstractBaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private IntegrationEventSerializationHandler handler;

    private final static UUID jobId = UUID.randomUUID();
    private final static UUID eventId = UUID.randomUUID();
    private final static Instant now = Instant.parse("2025-06-08T14:00:00Z");


    static Stream<Arguments> provideEvents() {
        return Stream.of(
                Arguments.of(new JobCreatedEvent(new IntegrationEventId(eventId), now, new JobId(jobId)),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobCreatedEvent) event1).getJobId().equals(((JobCreatedEvent) event2).getJobId())
                ),
                Arguments.of(new JobUpdatedEvent(new IntegrationEventId(eventId), now, new JobId(jobId)),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobUpdatedEvent) event1).getJobId().equals(((JobUpdatedEvent) event2).getJobId())
                ),
                Arguments.of(new JobDeletedEvent(new IntegrationEventId(eventId), now, new JobId(jobId)),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobDeletedEvent) event1).getJobId().equals(((JobDeletedEvent) event2).getJobId())
                ),

                Arguments.of(
                        new JobFieldUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), UpdateJobFieldCommand.Field.COMMENT),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobFieldUpdatedEvent) event1).getJobId().equals(((JobFieldUpdatedEvent) event2).getJobId())
                                && ((JobFieldUpdatedEvent) event1).getField().equals(((JobFieldUpdatedEvent) event2).getField())
                ),
                Arguments.of(
                        new JobStatusUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), JobStatus.PENDING),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobStatusUpdatedEvent) event1).getJobId().equals(((JobStatusUpdatedEvent) event2).getJobId())
                                && ((JobStatusUpdatedEvent) event1).getJobStatus().equals(((JobStatusUpdatedEvent) event2).getJobStatus())
                ),
                Arguments.of(
                        new JobRatingUpdatedEvent(IntegrationEventId.generate(), JobId.generate(), JobRating.of(4)),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((JobRatingUpdatedEvent) event1).getJobId().equals(((JobRatingUpdatedEvent) event2).getJobId())
                                && ((JobRatingUpdatedEvent) event1).getJobRating().equals(((JobRatingUpdatedEvent) event2).getJobRating())
                ),

                Arguments.of(
                        new ActivityCreatedEvent(IntegrationEventId.generate(), JobId.generate(), ActivityId.generate(), ActivityType.EMAIL),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>)  (event1, event2) ->
                                ((ActivityCreatedEvent) event1).getJobId().equals(((ActivityCreatedEvent) event2).getJobId())
                                && ((ActivityCreatedEvent) event1).getActivityId().equals(((ActivityCreatedEvent) event2).getActivityId())
                                && ((ActivityCreatedEvent) event1).getActivityType().equals(((ActivityCreatedEvent) event2).getActivityType())
                ),
                Arguments.of(
                        new ActivityAutomaticallyCreatedEvent(IntegrationEventId.generate(), JobId.generate(), ActivityId.generate(), ActivityType.EMAIL),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>)  (event1, event2) ->
                                ((ActivityAutomaticallyCreatedEvent) event1).getJobId().equals(((ActivityAutomaticallyCreatedEvent) event2).getJobId())
                                && ((ActivityAutomaticallyCreatedEvent) event1).getActivityId().equals(((ActivityAutomaticallyCreatedEvent) event2).getActivityId())
                                && ((ActivityAutomaticallyCreatedEvent) event1).getActivityType().equals(((ActivityAutomaticallyCreatedEvent) event2).getActivityType())
                ),


                Arguments.of(new AttachmentCreatedEvent(IntegrationEventId.generate(), JobId.generate(), AttachmentId.generate()),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((AttachmentCreatedEvent) event1).getJobId().equals(((AttachmentCreatedEvent) event2).getJobId())
                                && ((AttachmentCreatedEvent) event1).getAttachmentId().equals(((AttachmentCreatedEvent) event2).getAttachmentId())
                ),
                Arguments.of(new AttachmentDeletedEvent(IntegrationEventId.generate(), JobId.generate(), AttachmentId.generate()),
                        (BiFunction <IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((AttachmentDeletedEvent) event1).getJobId().equals(((AttachmentDeletedEvent) event2).getJobId())
                                && ((AttachmentDeletedEvent) event1).getAttachmentId().equals(((AttachmentDeletedEvent) event2).getAttachmentId())
                ),

                Arguments.of(new UserCreatedEvent(IntegrationEventId.generate(), UserId.generate()),
                        (BiFunction<IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                            ((UserCreatedEvent) event1).getUserId().equals(((UserCreatedEvent) event2).getUserId())
                ),
                Arguments.of(new UserUpdatedEvent(IntegrationEventId.generate(), UserId.generate()),
                        (BiFunction<IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((UserUpdatedEvent) event1).getUserId().equals(((UserUpdatedEvent) event2).getUserId())
                ),
                Arguments.of(new UserDeletedEvent(IntegrationEventId.generate(), UserId.generate()),
                        (BiFunction<IntegrationEvent, IntegrationEvent, Boolean>) (event1, event2) ->
                                ((UserDeletedEvent) event1).getUserId().equals(((UserDeletedEvent) event2).getUserId())
                )
        );
    }

    @Test
    void shouldSerializeIntegrationEventSubclasses() {
        Class<?>[] classes = IntegrationEvent.class.getPermittedSubclasses();

        // check that the provider method for the 'shouldMapEventsToTopics' test is accurate
        assertThat(provideEvents().count()).isEqualTo(classes.length);
    }

    @ParameterizedTest
    @MethodSource("provideEvents")
    <T extends IntegrationEvent> void shouldSerializeEvent(T event) throws JsonProcessingException {
        String json = handler.serialize(event);
        assertThat(json).isNotEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideEvents")
    <T extends IntegrationEvent> void shouldDeserializeEvent(T event, BiFunction <IntegrationEvent, IntegrationEvent, Boolean> check) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        MongoIntegrationEvent mongoEvent = new MongoIntegrationEvent();
        mongoEvent.setId(eventId);
        mongoEvent.setOccurredAt(now);
        mongoEvent.setStatus(EventStatus.PENDING);
        mongoEvent.setType(event.getClass().getSimpleName());
        mongoEvent.setPayload(payload);

        IntegrationEvent read = handler.readFromPayload(mongoEvent.getType(), mongoEvent.getPayload());
        assertThat(read.getId()).isEqualTo(event.getId());
        assertThat(read.getOccurredAt()).isEqualTo(event.getOccurredAt());
        assertThat(check.apply(event, read)).isTrue();
    }

    @Test
    void whenTypeUnknown_thenShouldThrowDeserializationException() {
        var ex = assertThrows(DeserializationException.class, () -> handler.readFromPayload("unknown", "{}"));
        assertThat(ex.getMessage()).contains("Unknown event type");
    }

    @Test
    void whenJsonMalformed_thenShouldThrowDeserializationException() {
        var ex = assertThrows(DeserializationException.class, () -> handler.readFromPayload("UserCreatedEvent", "{id:1}"));
        assertThat(ex.getMessage()).contains("Cannot process JSON");
    }
}