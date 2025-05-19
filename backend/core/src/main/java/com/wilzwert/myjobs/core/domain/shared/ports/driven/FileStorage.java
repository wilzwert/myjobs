package com.wilzwert.myjobs.core.domain.shared.ports.driven;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 * Date:22/03/2025
 * Time:14:57
 */
public interface FileStorage {
    DownloadableFile store(File file, String targetFilename, String originalFilename);
    void delete(String fileId);
    DownloadableFile retrieve(String fileId, String originalFilename);
    String generateProtectedUrl(String fileId);
}
