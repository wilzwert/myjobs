package com.wilzwert.myjobs.core.domain.exception;


import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:09:17
 */

public class NoHtmlFetcherException extends EntityNotFoundException {
    public NoHtmlFetcherException() {
        super(ErrorCode.NO_HTML_FETCHER_FOUND);
    }
}
