package com.wilzwert.myjobs.core.domain.shared.ports.driven;


import com.wilzwert.myjobs.core.domain.model.DownloadableFile;
import com.wilzwert.myjobs.core.domain.model.job.JobId;

import java.io.File;

/**
 * @author Wilhelm Zwertvaegher
 */
public interface FileStorage {
    DownloadableFile store(File file, String targetFilename, String originalFilename);
    void delete(String fileId);
    DownloadableFile retrieve(String fileId, String originalFilename);
    String generateProtectedUrl(JobId jobId, String fileId);
}
