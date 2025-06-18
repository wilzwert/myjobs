package com.wilzwert.myjobs.infrastructure.event.kafka;

import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityAutomaticallyCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.activity.event.integration.ActivityCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.attachment.event.integration.AttachmentDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.job.event.integration.*;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserUpdatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class KafkaIntegrationEventTopicResolver {
    private final Map<Class<? extends IntegrationEvent>, String> mapping = new HashMap<>();
    
    public KafkaIntegrationEventTopicResolver(KafkaProperties kafkaProperties) {

        String jobsTopic = kafkaProperties.getTopicPrefix()+"jobs";
        String usersTopic = kafkaProperties.getTopicPrefix()+"users";

        mapping.put(JobCreatedEvent.class, jobsTopic);
        mapping.put(JobUpdatedEvent.class, jobsTopic);
        mapping.put(JobDeletedEvent.class, jobsTopic);
        mapping.put(JobStatusUpdatedEvent.class, jobsTopic);
        mapping.put(JobRatingUpdatedEvent.class, jobsTopic);
        mapping.put(JobFieldUpdatedEvent.class, jobsTopic);
        mapping.put(AttachmentDeletedEvent.class, jobsTopic);
        mapping.put(AttachmentCreatedEvent.class, jobsTopic);
        mapping.put(ActivityAutomaticallyCreatedEvent.class, jobsTopic);
        mapping.put(ActivityCreatedEvent.class, jobsTopic);
        mapping.put(UserCreatedEvent.class, usersTopic);
        mapping.put(UserUpdatedEvent.class, usersTopic);
        mapping.put(UserDeletedEvent.class, usersTopic);
    }

    public String resolve(Class<? extends IntegrationEvent> eventClass) {
        return Optional.ofNullable(mapping.get(eventClass))
                .orElseThrow(() -> new IllegalArgumentException("Unknown event class " + eventClass));
    }

    public String resolve(IntegrationEvent event) {
        return resolve(event.getClass());
    }
}
