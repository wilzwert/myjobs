package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.model.job.ports.driving.ExtractJobMetadataUseCase;
import com.wilzwert.myjobs.core.domain.model.job.service.JobMetadataService;

/**
 * @author Wilhelm Zwertvaegher
 */
public class ExtractJobMetadataUseCaseImpl implements ExtractJobMetadataUseCase {

    private final JobMetadataService jobMetadataService;

    public ExtractJobMetadataUseCaseImpl(JobMetadataService jobMetadataService) {
        this.jobMetadataService = jobMetadataService;
    }

    @Override
    public JobMetadata extract(String url) {
        return this.jobMetadataService.extractMetadata(url);
    }
}
