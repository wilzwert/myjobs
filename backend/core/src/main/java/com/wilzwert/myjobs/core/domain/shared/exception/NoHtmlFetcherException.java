package com.wilzwert.myjobs.core.domain.shared.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 */

public class NoHtmlFetcherException extends DomainException {
    public NoHtmlFetcherException() {
        super(ErrorCode.NO_HTML_FETCHER_FOUND);
    }
}
