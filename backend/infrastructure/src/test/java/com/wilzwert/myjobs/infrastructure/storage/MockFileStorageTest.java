package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.Attachment;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 */
public class MockFileStorageTest {

    private final FileStorage underTest = new MockFileStorage();

    @Test
    void testMockFileStorage() {
        File file = new File("src/test/resources/cv_test.doc"); // Remplace avec ton fichier test

        // Test du store
        DownloadableFile uploadedFile = underTest.store(file, "uploads/cv.doc", "testfile.doc");
        assertNotNull(uploadedFile);
        assertEquals("faked_uploads/cv.doc", uploadedFile.fileId());
        assertEquals("faked_testfile.doc", uploadedFile.filename());
        assertEquals("faked_uploads/cv.doc", uploadedFile.path());
        assertEquals("application/msword", uploadedFile.contentType());

        // Test de la récupération
        Attachment attachment = Attachment.builder()
                .id(AttachmentId.generate())
                .fileId(uploadedFile.path())
                .name("test file")
                .filename("testfile.doc")
                .contentType("application/msword")
                .build();
        String url = underTest.generateProtectedUrl(JobId.generate(), attachment);
        assertEquals("https://mockstorage.local/fake-url/faked_uploads/cv.doc", url);

        // Test de la suppression
        underTest.delete(attachment.getFileId());
    }
}