package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/04/2025
 * Time:09:44
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "dev|test")
@ExtendWith(MockitoExtension.class)
public class S3FileStorageTest {
    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3FileStorage underTest;

    @BeforeEach
    public void setUp() {
        underTest = new S3FileStorage(s3Client, s3Presigner, "test");
    }

    @Test
    public void shouldReturnContentType() {
        assertThat(underTest.getContentType("document.doc")).isEqualTo("application/msword");
        assertThat(underTest.getContentType("document.pdf.jpg")).isEqualTo("image/jpeg");
        assertThat(underTest.getContentType("document.pdf")).isEqualTo("application/pdf");
        assertThat(underTest.getContentType("document")).isEqualTo("application/octet-stream");
    }

    @Test
    void testS3FileStorage() {
        File file = new File("src/test/resources/cv_test.doc"); // Remplace avec ton fichier test

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(PutObjectResponse.builder().build());
        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(DeleteObjectResponse.builder().build());


        // Store
        DownloadableFile uploadedFile = underTest.store(file, "uploads/cv.doc", "testfile.doc");
        assertNotNull(uploadedFile);
        assertEquals("uploads/cv.doc", uploadedFile.fileId());
        assertEquals("testfile.doc", uploadedFile.filename());
        assertEquals("uploads/cv.doc", uploadedFile.path());
        assertEquals("application/msword", uploadedFile.contentType());

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // Delete
        underTest.delete("uploads/cv.doc");
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));

    }
}