package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private SecureTempFileHelper secureTempFileHelper;

    @Mock
    private S3Client s3Client;
    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private ResponseInputStream<GetObjectResponse> responseInputStream;

    private S3FileStorage underTest;

    @BeforeEach
    void setUp() {
        secureTempFileHelper = new SecureTempFileHelper();
        MockitoAnnotations.openMocks(this);
        underTest = new S3FileStorage(s3Client, s3Presigner, "test", secureTempFileHelper);
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

        String url = underTest.generateProtectedUrl(JobId.generate(), AttachmentId.generate(), "fileId");

        assertEquals("https://example.com/fake-url", url);
    }

    @Test
    void shouldRetrieveFileFromS3Successfully() throws IOException {
        S3FileStorage fileStorage = new S3FileStorage(s3Client, s3Presigner, "bucket", secureTempFileHelper);
        // Arrange
        String fileId = "uploads/file.pdf";
        String originalFilename = "file.pdf";
        byte[] fileContent = "Hello world".getBytes();

        Path tempFilePath = Files.createTempFile("test", ".pdf");
        File tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit();

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);
        when(responseInputStream.readAllBytes()).thenReturn(fileContent); // alternative to IoUtils

        // Act
        DownloadableFile file = fileStorage.retrieve(fileId, originalFilename);

        // Assert
        assertThat(file.fileId()).isEqualTo(fileId);
        assertThat(file.path()).endsWith("s3");
        assertThat(file.contentType()).isEqualTo("application/pdf");
        assertThat(file.filename()).isEqualTo(originalFilename);
    }

    @Test
    void shouldThrowStorageExceptionWhenSdkClientExceptionOccurs() {
        String fileId = "badfile";
        String originalFilename = "badfile.txt";

        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(SdkClientException.create("S3 error"));

        var ex = assertThrows(StorageException.class, () ->
                underTest.retrieve(fileId, originalFilename)
        );
        assertThat(ex.getMessage()).isEqualTo("Unable to retrieve file from S3 bucket");
    }

    @Test
    void shouldThrowStorageExceptionWhenIOExceptionOccurs() throws IOException {
        String fileId = "badfile";
        String originalFilename = "badfile.txt";
        byte[] fileContent = "Hello world".getBytes();

        Path tempFilePath = Files.createTempFile("badfile", ".txt");
        File tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit();

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseInputStream);
        when(responseInputStream.readAllBytes()).thenReturn(fileContent);
        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            filesMockedStatic.when(() -> Files.createTempFile(anyString(), anyString(), any()))
                    .thenReturn(tempFilePath);
            filesMockedStatic.when(() -> Files.write(any(Path.class), eq(fileContent))).thenThrow(IOException.class);
            var ex = assertThrows(StorageException.class, () ->
                    underTest.retrieve(fileId, originalFilename)
            );
            assertThat(ex.getMessage()).isEqualTo("Unable to store file downloaded from S3 bucket");
        }
    }
}