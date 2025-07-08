package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 */
@Slf4j
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final SecureTempFileHelper secureTempFileHelper;


    public S3FileStorage(S3Client s3Client, S3Presigner s3Presigner, String bucketName, SecureTempFileHelper secureTempFileHelper) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
        this.secureTempFileHelper = secureTempFileHelper;
    }

    @Override
    public DownloadableFile store(File file, String targetFilename, String originalFilename) {
        log.debug("Putting file to S3 bucket {}", bucketName);
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(targetFilename)
                            .contentDisposition("inline; filename=\"" + originalFilename + "\"")
                            .build(),
                    RequestBody.fromFile(file));
        }
        catch (Exception e) {
            log.error("Unable to put file to S3 Bucket {}", bucketName, e);
            throw new StorageException("Failed to store file in S3 bucket", e);
        }
        // use the targetfilename as key and fileId
        return new DownloadableFile(targetFilename, targetFilename, getContentType(originalFilename), originalFilename);
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
        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileId)
                    .build());
            File tempFile = Files.createTempFile("temp", "s3", secureTempFileHelper.getFileAttribute()).toFile();
            Files.write(Path.of(tempFile.getPath()), responseInputStream.readAllBytes());

            // for now, target path and fileId are the same
            return new DownloadableFile(fileId, tempFile.getPath(), getContentType(originalFilename), originalFilename);
        }
        catch (SdkClientException e) {
            throw new StorageException("Unable to retrieve file from S3 bucket", e);
        }
        catch (IOException e) {
            throw new StorageException("Unable to store file downloaded from S3 bucket", e);
        }
    }

    @Override
    public String generateProtectedUrl(JobId jobId, AttachmentId attachmentId, String fileId) {
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

    public String getContentType(String originalFilename) {
        // get file MIME type
        String contentType = null;

        Optional<MediaType> mimeTypeOptional = MediaTypeFactory.getMediaType(originalFilename);
        if(mimeTypeOptional.isPresent()) {
            contentType = mimeTypeOptional.get().toString();
        }

        if (contentType == null) {
            contentType = "application/octet-stream"; // default value
        }

        return contentType;
    }
}
