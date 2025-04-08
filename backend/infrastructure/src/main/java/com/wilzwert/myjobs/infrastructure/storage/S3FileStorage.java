package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.exception.AttachmentFileNotReadableException;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.ports.driven.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 * Date:21/03/2025
 * Time:16:07
 */
@Slf4j
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;


    public S3FileStorage(S3Client s3Client, S3Presigner s3Presigner, String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    @Override
    public DownloadableFile store(File file, String targetFilename, String originalFilename) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(targetFilename)
                            .contentDisposition("attachment; filename=\"" + originalFilename + "\"")
                            .build(),
                            RequestBody.fromFile(file));
            // use the targetfilename as key and fileId
            return new DownloadableFile(targetFilename, targetFilename, getContentType(originalFilename, file.getPath()), originalFilename);
        }
        catch (IOException e) {
            // TODO improve exception management
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileId) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build());
    }

    @Override
    public DownloadableFile retrieve(String fileId, String originalFilename) {
        Path filePath = Paths.get(fileId);
        FileSystemResource resource = new FileSystemResource(filePath.toFile());
        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentFileNotReadableException();
        }

        try {
            return new DownloadableFile(fileId, filePath.toString(), getContentType(originalFilename, fileId), originalFilename);
        }
        catch (IOException e) {
            throw new AttachmentFileNotReadableException();
        }
    }

    @Override
    public String generateProtectedUrl(String fileId) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileId)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(30))
                .getObjectRequest(getRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }

    private String getContentType(String originalFilename, String filePath) throws IOException {
        // get file MIME type
        String contentType = Files.probeContentType(Paths.get(filePath));
        if(contentType == null) {
            Optional<MediaType> mimeTypeOptional = MediaTypeFactory.getMediaType(originalFilename);
            if(mimeTypeOptional.isPresent()) {
                contentType = mimeTypeOptional.get().toString();
            }
        }
        if (contentType == null) {
            contentType = "application/octet-stream"; // default value
        }

        return contentType;
    }
}
