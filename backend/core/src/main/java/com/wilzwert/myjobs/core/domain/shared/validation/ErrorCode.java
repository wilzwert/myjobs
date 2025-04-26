package com.wilzwert.myjobs.core.domain.shared.validation;


/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:15:18
 *
 * Error codes used in validation errors
 */
public enum ErrorCode {
    // common errors
    VALIDATION_FAILED,
    FIELD_CANNOT_BE_EMPTY,
    INVALID_EMAIL,
    INVALID_URL,
    FIELD_TOO_SHORT,
    FIELD_TOO_LONG,
    PAGINATION_INVALID_PAGE,
    PAGINATION_INVALID_PAGE_SIZE,
    PAGINATION_OFFSET_TOO_BIG,

    // fetch / extract parse jobs specific errors
    NO_HTML_FETCHER_FOUND,
    NO_METADATA_EXTRACTOR_FOUND,


    // user specific errors
    USER_WEAK_PASSWORD,
    USER_ALREADY_EXISTS,
    USER_USERNAME_ALREADY_TAKEN,
    USER_NOT_FOUND,
    USER_LOGIN_FAILED,
    USER_PASSWORD_MATCH_FAILED,
    USER_PASSWORD_RESET_EXPIRED,

    // attachment specific errors
    ATTACHMENT_NOT_FOUND,
    ATTACHMENT_FILE_NOT_READABLE,

    // job specific errors
    JOB_ALREADY_EXISTS,
    JOB_NOT_FOUND,

}
