package com.wilzwert.myjobs.infrastructure.serialization.jackson;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserCreatedEvent;
import com.wilzwert.myjobs.core.domain.shared.event.integration.IntegrationEventId;
import com.wilzwert.myjobs.infrastructure.serialization.exception.SerializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegrationEventSerializationHandlerTest {

    @Mock
    private ObjectMapper objectMapper;


    @InjectMocks
    private JacksonIntegrationEventSerializationHandler handler;

    @Test
    void whenJsonJsonProcessingException_thenShouldThrowSerializationException() throws JacksonException {
        UserCreatedEvent event = new UserCreatedEvent(IntegrationEventId.generate(), UserId.generate());
        when(objectMapper.writeValueAsString(event)).thenThrow(JacksonException.class);

        var ex = assertThrows(SerializationException.class, () -> handler.serialize(event));
        assertThat(ex.getMessage()).contains("Serialization failed");
    }
}