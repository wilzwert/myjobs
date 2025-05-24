package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import com.wilzwert.myjobs.infrastructure.configuration.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Wilhelm Zwertvaegher
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "integration")
public class S3FileStorageIT extends AbstractBaseIntegrationTest {

    @Autowired
    private FileStorage fileStorage;


    @Test
    void testFileStorage() {
        // TODO
        /*
        File file = new File("src/test/resources/cv_test.doc");

        // testing storage
        DownloadableFile uploadedFile = fileStorage.store(file, "uploads/cv.doc", "testfile.doc");
        assertNotNull(uploadedFile);
        assertEquals("testfile.doc", uploadedFile.filename());

        // testing retrieval
        String fileId = uploadedFile.path(); // Utilise le fileId retourné
        String url = fileStorage.generateProtectedUrl(fileId);
        assertNotNull(url);
        System.out.println("URL signée : " + url);

        // Test de la suppression
        fileStorage.delete(fileId);*/
    }

}
