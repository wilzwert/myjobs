package com.wilzwert.myjobs.core.domain.shared.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

public class IncompleteAggregateException extends DomainException {
    public IncompleteAggregateException() {super(ErrorCode.INCOMPLETE_AGGREGATE);}
}
