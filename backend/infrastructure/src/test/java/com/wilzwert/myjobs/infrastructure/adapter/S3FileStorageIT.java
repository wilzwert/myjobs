package com.wilzwert.myjobs.infrastructure.adapter;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.ports.driven.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:09:44
 */
@SpringBootTest
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "staging")
public class S3FileStorageIT {

    @Autowired
    private FileStorage fileStorage;


    @Test
    void testFileStorage() {
        File file = new File("src/test/resources/cv_test.doc"); // Remplace avec ton fichier test

        // Test du store
        DownloadableFile uploadedFile = fileStorage.store(file, "uploads/cv.doc", "testfile.doc");
        assertNotNull(uploadedFile);
        assertEquals("testfile.doc", uploadedFile.filename());

        // Test de la récupération
        String fileId = uploadedFile.path(); // Utilise le fileId retourné
        String url = fileStorage.generateProtectedUrl(fileId);
        assertNotNull(url);
        System.out.println("URL signée : " + url);

        // Test de la suppression
        fileStorage.delete(fileId);
    }

}
