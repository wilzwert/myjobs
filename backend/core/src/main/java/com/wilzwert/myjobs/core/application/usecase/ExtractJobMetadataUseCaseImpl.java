package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driving.ExtractJobMetadataUseCase;
import com.wilzwert.myjobs.core.domain.service.metadata.JobMetadataExtractorService;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
 */
public class ExtractJobMetadataUseCaseImpl implements ExtractJobMetadataUseCase {

    private final JobMetadataExtractorService jobMetadataExtractorService;

    public ExtractJobMetadataUseCaseImpl(JobMetadataExtractorService jobMetadataExtractorService) {
        this.jobMetadataExtractorService = jobMetadataExtractorService;
    }

    @Override
    public JobMetadata extract(String url) {
        return this.jobMetadataExtractorService.extractMetadata(url);
    }
}
