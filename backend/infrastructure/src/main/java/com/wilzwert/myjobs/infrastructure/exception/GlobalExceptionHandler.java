package com.wilzwert.myjobs.infrastructure.exception;

import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;


/**
 * Global exception handler to intercept several types of Exceptions
 * Log unexpected  exceptions
 * Set http response status code accordingly
 * @author Wilhelm Zwertvaegher
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> generateError(EntityAlreadyExistsException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> generateError(EntityNotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    /**
     * @param ex a ValidationException
     * @return the response entity
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> generateError(ValidationException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> generateError(HandlerMethodValidationException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> generateError(LoginException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> generateError(DomainException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> generateResponseStatusException(ResponseStatusException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> generateMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> generateConstraintViolation(ConstraintViolationException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> generateAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> generateHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ErrorResponse> generateHttpMediaTypeException(HttpMediaTypeException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }


    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> generateNumberFormatException(NumberFormatException ex) {
       ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> generateBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> generateBadRequestException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ErrorResponse> generateHttpClientErrorException(HttpClientErrorException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> generateHttpClientErrorException(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> generateIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generateInternalErrorException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }
}