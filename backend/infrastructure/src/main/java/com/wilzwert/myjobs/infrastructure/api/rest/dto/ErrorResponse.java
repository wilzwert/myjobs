package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wilzwert.myjobs.core.domain.exception.*;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @JsonIgnore
    private HttpStatusCode httpStatusCode;

    private String status;
    private String message;
    private Map<String, List<String>> errors;
    private String time;

    public static <E extends EntityAlreadyExistsException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.CONFLICT, ex.getErrorCode().name());
    }

    public static <E extends EntityNotFoundException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.NOT_FOUND, ex.getErrorCode().name());
    }

    public static ErrorResponse fromException(ValidationException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        for (ValidationError error : ex.getFlatErrors()) {
            errors.computeIfAbsent(error.field(), k -> new ArrayList<>()).add(error.code().name());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    public static ErrorResponse fromException(DomainException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getErrorCode().name());
    }

    public static ErrorResponse fromException(ResponseStatusException ex) {
        return build(ex.getStatusCode(), ex.getReason());
    }

    public static ErrorResponse fromException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, List<String>> errors = new HashMap<>();

        for(FieldError fieldError : fieldErrors){
            errors.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>()).add(fieldError.getDefaultMessage());
        }
        return build(ex.getStatusCode(), "Validation error", errors);
    }

    public static ErrorResponse fromException(LoginException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Access denied");
    }

    public static ErrorResponse fromException(AccessDeniedException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Access denied");
    }

    public static ErrorResponse fromException(HttpRequestMethodNotSupportedException ex) {
        return build(ex.getStatusCode(), "Unsupported method");
    }

    public static ErrorResponse fromException(NumberFormatException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(HttpMediaTypeException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    public static ErrorResponse fromException(HttpClientErrorException ex) {
        return build(ex.getStatusCode(), ex.getMessage());
    }

    public static ErrorResponse fromException(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred.");
    }

    private static ErrorResponse build(HttpStatusCode status, String message) {
        return new ErrorResponse(status, String.valueOf(status.value()), message, Collections.emptyMap(), new Date().toString());
    }

    private static ErrorResponse build(HttpStatusCode status, String message, Map<String, List<String>> errors) {
        return new ErrorResponse(status, String.valueOf(status.value()), message, errors, new Date().toString());
    }


}
