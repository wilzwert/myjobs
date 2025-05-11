package com.wilzwert.myjobs.core.application.usecase;


import com.wilzwert.myjobs.core.domain.model.job.JobMetadata;
import com.wilzwert.myjobs.core.domain.ports.driving.ExtractJobMetadataUseCase;
import com.wilzwert.myjobs.core.domain.service.job.JobMetadataService;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:15:27
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
