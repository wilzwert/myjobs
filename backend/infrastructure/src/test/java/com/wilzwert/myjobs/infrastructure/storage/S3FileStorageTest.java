package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Wilhelm Zwertvaegher
 */
@EnabledIfSystemProperty(named = "spring.profiles.active", matches = "dev|test")
@ExtendWith(MockitoExtension.class)
public class S3FileStorageTest {
    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;

    private S3FileStorage underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new S3FileStorage(s3Client, s3Presigner, "test");
    }

    @Test
    void shouldReturnContentType() {
        assertThat(underTest.getContentType("document.doc")).isEqualTo("application/msword");
        assertThat(underTest.getContentType("document.pdf.jpg")).isEqualTo("image/jpeg");
        assertThat(underTest.getContentType("document.pdf")).isEqualTo("application/pdf");
        assertThat(underTest.getContentType("document")).isEqualTo("application/octet-stream");
    }

    @Test
    void testS3FileStorage() {
        File file = new File("src/test/resources/cv_test.doc");

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

    @Test
    void shouldGenerateProtectedUrl() throws MalformedURLException {
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(URI.create("https://example.com/fake-url").toURL());
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        String url = underTest.generateProtectedUrl("fileId");

        assertEquals("https://example.com/fake-url", url);
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionWhenRetrieve() {
        S3FileStorage fileStorage = new S3FileStorage(s3Client, s3Presigner, "bucket");

        assertThrows(UnsupportedOperationException.class, () ->
                fileStorage.retrieve("fileId", "original.txt")
        );
    }
}