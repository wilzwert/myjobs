package com.wilzwert.myjobs.core.domain.shared.validation;


/**
 * @author Wilhelm Zwertvaegher
 *
 * Error codes used in validation errors
 */
public enum ErrorCode {
    // common errors
    INCOMPLETE_AGGREGATE,
    VALIDATION_FAILED,
    FIELD_CANNOT_BE_NULL,
    FIELD_CANNOT_BE_EMPTY,
    INVALID_VALUE,
    INVALID_EMAIL,
    INVALID_URL,
    FIELD_TOO_SHORT,
    FIELD_TOO_LONG,
    FIELD_MIN_MAX_LENGTH,
    FIELD_VALUE_TOO_SMALL,
    FIELD_VALUE_TOO_BIG,
    PAGINATION_INVALID_PAGE,
    PAGINATION_INVALID_PAGE_SIZE,
    PAGINATION_OFFSET_TOO_BIG,
    UNEXPECTED_ERROR,
    CANNOT_SEND_MESSAGE,

    // fetch / extract parse jobs specific errors
    NO_HTML_FETCHER_FOUND,
    NO_METADATA_EXTRACTOR_FOUND,


    // user specific errors
    USER_WEAK_PASSWORD,
    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,
    USER_LOGIN_FAILED,
    USER_PASSWORD_MATCH_FAILED,
    USER_PASSWORD_RESET_EXPIRED,
    USER_DELETE_FAILED,

    // activity specific errors
    ACTIVITY_NOT_FOUND,

    // attachment specific errors
    ATTACHMENT_NOT_FOUND,
    ATTACHMENT_FILE_NOT_READABLE,

    // job specific errors
    JOB_ALREADY_EXISTS,
    JOB_NOT_FOUND,

}
