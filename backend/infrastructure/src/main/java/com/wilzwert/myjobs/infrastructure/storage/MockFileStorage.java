package com.wilzwert.myjobs.infrastructure.storage;

import com.wilzwert.myjobs.core.domain.model.attachment.exception.AttachmentFileNotReadableException;
import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.shared.ports.driven.FileStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Profile("test")
@Component
public class MockFileStorage implements FileStorage {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public DownloadableFile store(File file, String targetFilename, String originalFilename) {
        try {
            byte[] content = java.nio.file.Files.readAllBytes(file.toPath());
            String fakedTargetFilename = "faked_"+targetFilename;
            storage.put(fakedTargetFilename, content);
            return new DownloadableFile(fakedTargetFilename, fakedTargetFilename, getContentType(file.toPath().toString(), originalFilename), "faked_"+originalFilename);
        } catch (Exception e) {
            throw new RuntimeException("Mock storage failed", e);
        }
    }

    @Override
    public void delete(String fileId) {
        storage.remove(fileId);
    }

    @Override
    public DownloadableFile retrieve(String fileId, String originalFilename) {
        byte[] content = storage.get(fileId);
        if (content == null) throw new RuntimeException("File not found in mock storage");

        try {
            return new DownloadableFile(fileId, originalFilename, getContentType(originalFilename, originalFilename), originalFilename);
        }
        catch (IOException e) {
            throw new AttachmentFileNotReadableException();
        }
    }

    @Override
    public String generateProtectedUrl(String fileId) {
        return "https://mockstorage.local/fake-url/"+fileId;
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
