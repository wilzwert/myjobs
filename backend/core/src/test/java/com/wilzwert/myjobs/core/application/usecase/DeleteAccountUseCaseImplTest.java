package com.wilzwert.myjobs.core.application.usecase;

import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.Job;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.User;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserDeleteException;
import com.wilzwert.myjobs.core.domain.model.user.exception.UserNotFoundException;
import com.wilzwert.myjobs.core.domain.model.user.ports.driven.UserDataManager;
import com.wilzwert.myjobs.core.domain.model.user.ports.driving.DeleteAccountUseCase;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class DeleteAccountUseCaseImplTest {

    private UserDataManager userDataManager;
    private FileStorage fileStorage;
    private DeleteAccountUseCase useCase;

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
    @BeforeEach
    void setUp() {
        userDataManager = mock(UserDataManager.class);
        fileStorage = mock(FileStorage.class);
        useCase = new DeleteAccountUseCaseImpl(userDataManager, fileStorage);
    }

    @Test
    void shouldDeleteUserAndAllAttachmentsSuccessfully() {
        // given
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));

        // when
        useCase.deleteAccount(userId);

        // then
        verify(fileStorage).delete(attachment.getFileId());
        verify(userDataManager).deleteUser(user);
    }

    @Test
    void shouldThrowUserDeleteExceptionWhenFileDeletionFails() {
        when(userDataManager.findById(userId)).thenReturn(Optional.of(user));
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
