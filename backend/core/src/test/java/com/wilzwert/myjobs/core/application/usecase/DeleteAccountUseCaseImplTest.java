package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.event.integration.UserDeletedEvent;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserDeleteException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;

import com.wilzwert.myjobs.core.domain.shared.ports.driven.event.IntegrationEventPublisher;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.transaction.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DeleteAccountUseCaseImplTest {

    @Mock
    private TransactionProvider transactionProvider;

    @Mock
    private IntegrationEventPublisher integrationEventPublisher;

    @Mock
    private UserDataManager userDataManager;

    @Mock
    private FileStorage fileStorage;

    @InjectMocks
    private DeleteAccountUseCaseImpl useCase;

    private final UserId userId = UserId.generate();
    private final Attachment attachment = Attachment.builder()
            .id(AttachmentId.generate())
            .filename("cv.pdf")
            .name("CV")
            .contentType("application/pdf")
            .fileId("fileId")
            .build();
    private final Job job = Job.builder()
            .id(JobId.generate())
            .userId(userId)
            .title("Job title")
            .description("Job description")
            .company("Company")
            .url("http://www.example.com")
            .attachments(List.of(attachment))
            .activities(List.of()).build();
    private final User user = User.builder()
            .id(userId)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .username("john")
            .password("password")
            .jobs(List.of(job)).build();

    @Test
    void shouldDeleteUserAndAllAttachmentsSuccessfully() {
        // given
        ArgumentCaptor<UserDeletedEvent> userDeletedEventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));
        when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
        when(integrationEventPublisher.publish(userDeletedEventCaptor.capture())).thenAnswer(i -> i.getArgument(0));


        // when
        useCase.deleteAccount(userId);

        // then
        verify(fileStorage).delete(attachment.getFileId());
        verify(userDataManager).deleteUser(user);
        verify(transactionProvider).executeInTransaction(any(Supplier.class));

        UserDeletedEvent userDeletedEvent = userDeletedEventCaptor.getValue();
        assertNotNull(userDeletedEvent);
        assertEquals(user.getId(), userDeletedEvent.getUserId());
    }

    @Test
    void shouldThrowUserDeleteExceptionWhenFileDeletionFails() {
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));
        when(transactionProvider.executeInTransaction(any(Supplier.class))).thenAnswer(i -> ((Supplier<?>)i.getArgument(0)).get());
        doThrow(new RuntimeException("S3 failure")).when(fileStorage).delete("fileId");

        // when / then
        assertThrows(UserDeleteException.class, () -> useCase.deleteAccount(userId));
        verify(fileStorage).delete("fileId");
        verify(userDataManager, never()).deleteUser(user);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
        // given
        when(userDataManager.findById(userId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(UserNotFoundException.class, () -> useCase.deleteAccount(userId));

        verifyNoInteractions(fileStorage);
        verify(userDataManager, never()).deleteUser(any());
    }
}
