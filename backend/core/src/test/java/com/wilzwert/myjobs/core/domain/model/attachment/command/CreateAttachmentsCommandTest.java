package com.wilzwert.myjobs.core.domain.model.attachment.command;

import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateAttachmentsCommandTest {

    @Test
    void builderShouldCreateCommandWithGivenFields() {
        // Arrange - objets fictifs pour le test
        CreateAttachmentCommand attachment1 = new CreateAttachmentCommand("File", new File("path1"), "file1");
        CreateAttachmentCommand attachment2 = new CreateAttachmentCommand("File 2", new File("path2"), "file1");

        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();

        List<CreateAttachmentCommand> attachments = List.of(attachment1, attachment2);

        CreateAttachmentsCommand command = new CreateAttachmentsCommand.Builder()
                .commandList(attachments)
                .userId(userId)
                .jobId(jobId)
                .build();

        assertEquals(attachments, command.createAttachmentCommandList());
        assertEquals(userId, command.userId());
        assertEquals(jobId, command.jobId());
    }

    @Test
    void builderShouldCopyFromExistingCommand() {
        // Arrange
        UserId userId = UserId.generate();
        JobId jobId = JobId.generate();
        List<CreateAttachmentCommand> attachments = List.of(
                new CreateAttachmentCommand("File", new File("path"), "file")
        );

        CreateAttachmentsCommand original = new CreateAttachmentsCommand(attachments, userId, jobId);

        CreateAttachmentsCommand copy = new CreateAttachmentsCommand.Builder(original).build();

        assertEquals(original.createAttachmentCommandList(), copy.createAttachmentCommandList());
        assertEquals(original.userId(), copy.userId());
        assertEquals(original.jobId(), copy.jobId());
    }
}
