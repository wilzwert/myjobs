package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentFileNotReadableException;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Wilhelm Zwertvaegher
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "dev|test")
class LocalFileStorageTest {

    private LocalFileStorage fileStorage;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // set "uploads/" in a temp dir
        fileStorage = new LocalFileStorage("http://localhost:8080", tempDir.resolve("uploads"));
    }

    @Test
    void shouldStoreAndRetrieveFile() throws IOException {
        // Arrange
        File tempFile = new File(tempDir.toFile(), "test.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, World!");
        }
        String targetFilename = "stored-test.txt";

        DownloadableFile stored = fileStorage.store(tempFile, targetFilename, "test.txt");

        assertNotNull(stored);
        assertEquals("text/plain", stored.contentType());
        assertTrue(new File(stored.fileId()).exists());

        // Retrieve the file
        DownloadableFile retrieved = fileStorage.retrieve(stored.fileId(), "test.txt");
        assertEquals(stored.fileId(), retrieved.fileId());
    }

    @Test
    void shouldDeleteFile() throws IOException {
        File tempFile = new File(tempDir.toFile(), "to-delete.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("To be deleted");
        }
        String targetFilename = "to-delete.txt";
        DownloadableFile stored = fileStorage.store(tempFile, targetFilename, "to-delete.txt");

        fileStorage.delete(stored.fileId());

        assertFalse(new File(stored.fileId()).exists());
    }

    @Test
    void retrieveNonExistentFile_shouldThrowException() {
        String invalidFileId = tempDir.resolve("non-existent.txt").toString();

        assertThrows(AttachmentFileNotReadableException.class, () ->
            fileStorage.retrieve(invalidFileId, "non-existent.txt")
        );
    }

    @Test
    void shouldGenerateProtectedUrl() {
        JobId jobId = new JobId(UUID.randomUUID());
        AttachmentId attachmentId = new AttachmentId(UUID.randomUUID());
        String fileId = "some-file-id";

        String url = fileStorage.generateProtectedUrl(jobId, attachmentId, fileId);

        assertEquals("http://localhost:8080/api/jobs/" + jobId.value() + "/attachments/" + attachmentId.value() + "/file", url);
    }
}