package com.wilzwert.myjobs.core.domain.model.job.exception;


import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class NoJobMetadataExtractorException extends DomainException {
    public NoJobMetadataExtractorException() {
        super(ErrorCode.NO_METADATA_EXTRACTOR_FOUND);
    }
}
