package com.wilzwert.myjobs.infrastructure.storage;


import com.wilzwert.myjobs.core.domain.model.attachment.AttachmentId;
import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentFileNotReadableException;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.job.JobId;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


/**
 * @author Wilhelm Zwertvaegher
 */
@Profile("dev")
@Component
public class LocalFileStorage implements FileStorage {

    private final Path storageLocation; // Dossier local

    private final String backendUrl;

    @Autowired
    public LocalFileStorage(@Value("${application.backend.url}") final String backendUrl) {
        this(backendUrl, Paths.get("uploads"));
    }

    public LocalFileStorage(String backendUrl, Path storageLocation) {
        try {
            if (!Files.exists(storageLocation)) {
                Files.createDirectories(storageLocation); // Crée le répertoire s'il n'existe pas
            }
        } catch (IOException e) {
            throw new StorageException("Failed to initialize local storage", e);
        }
        this.storageLocation = storageLocation;
        this.backendUrl = backendUrl;
    }

    @Override
    public DownloadableFile store(File file, String targetFilename, String originalFilename) {
        try {
            Path targetLocation = storageLocation.resolve(targetFilename);
            Path directory = targetLocation.getParent();
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Files.copy(file.toPath(), targetLocation);
            // we use the targetlocation as fileid
            return new DownloadableFile(targetLocation.toString(), targetLocation.toString(), getContentType(originalFilename, targetLocation.toString()), "");
        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            Files.delete(Paths.get(fileId));
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }

    @Override
    public DownloadableFile retrieve(String fileId, String originalFilename) {
        Path filePath = Paths.get(fileId);
        FileSystemResource resource = new FileSystemResource(filePath.toFile());
        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentFileNotReadableException();
        }

        try {
            // for now, target path and fileId are the same
            return new DownloadableFile(filePath.toString(), filePath.toString(), getContentType(originalFilename, fileId), originalFilename);
        }
        catch (IOException e) {
            throw new AttachmentFileNotReadableException();
        }
    }

    @Override
    public String generateProtectedUrl(JobId jobId, AttachmentId attachmentId, String fileId) {
        return backendUrl+"/api/jobs/"+jobId.value().toString()+"/attachments/"+attachmentId.value().toString()+"/file";
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
