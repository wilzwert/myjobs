package com.wilzwert.myjobs.infrastructure.api.rest.exception;

import com.wilzwert.myjobs.core.domain.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.LoginException;
import com.wilzwert.myjobs.core.domain.exception.PasswordMatchException;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler to intercept several types of Exceptions
 * and set http response status code accordingly
 * @author Wilhelm Zwertvaegher
 */
@ControllerAdvice
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

    @ExceptionHandler(PasswordMatchException.class)
    public ResponseEntity<ErrorResponse> generateError(PasswordMatchException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ErrorResponse> generateError(LoginException ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> generateResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ErrorResponse.fromException(ex), ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> generateMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generateInternalErrorException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.fromException(ex);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatusCode());
    }
}