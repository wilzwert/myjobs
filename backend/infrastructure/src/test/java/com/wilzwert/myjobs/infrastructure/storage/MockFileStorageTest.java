package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
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
        String fileId = uploadedFile.path(); // Utilise le fileId retourné
        String url = underTest.generateProtectedUrl(fileId);
        assertEquals("https://mockstorage.local/fake-url/faked_uploads/cv.doc", url);

        // Test de la suppression
        underTest.delete(fileId);
    }
}