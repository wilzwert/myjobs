package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wilzwert.myjobs.core.domain.model.user.exception.LoginException;
import com.wilzwert.myjobs.core.domain.shared.exception.DomainException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.shared.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.shared.exception.ValidationException;
import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @JsonIgnore
    private HttpStatusCode httpStatusCode;

    private String status;
    private String message;
    private Map<String, List<ValidationErrorResponse>> errors;
    private long timestamp;

    public static <E extends EntityAlreadyExistsException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.CONFLICT, ex.getErrorCode().name());
    }

    public static <E extends EntityNotFoundException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.NOT_FOUND, ex.getErrorCode().name());
    }

    public static ErrorResponse fromException(HandlerMethodValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED.name());
    }

    public static ErrorResponse fromException(ValidationException ex) {
        Map<String, List<ValidationErrorResponse>> errors = new HashMap<>();
        for (ValidationError error : ex.getFlatErrors()) {
            errors.computeIfAbsent(error.field(), k -> new ArrayList<>()).add(new ValidationErrorResponse(error.code().name(), error.details()));
        }
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_FAILED.name(), errors);
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
        Map<String, List<ValidationErrorResponse>> errors = new HashMap<>();

        for(FieldError fieldError : fieldErrors){
            errors.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>()).add(new ValidationErrorResponse(fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : ErrorCode.UNEXPECTED_ERROR.name()));
        }
        return build(ex.getStatusCode(), ErrorCode.VALIDATION_FAILED.name(), errors);
    }

    public static ErrorResponse fromException(ConstraintViolationException ex) {
        Map<String, List<ValidationErrorResponse>> errors = new HashMap<>();

        for(ConstraintViolation<?> violation : ex.getConstraintViolations()){
            errors.computeIfAbsent(violation.getPropertyPath().toString(), k -> new ArrayList<>()).add(new ValidationErrorResponse(violation.getMessage() != null ? violation.getMessage() : ErrorCode.UNEXPECTED_ERROR.name()));
        }
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.VALIDATION_FAILED.name(), errors);
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
        Throwable cause = ex.getCause();
        if (cause instanceof JsonMappingException formatEx) {
            String fieldName = "";
            if (!formatEx.getPath().isEmpty()) {
                fieldName = formatEx.getPath().getFirst().getFieldName();
            }
            if(fieldName != null && !fieldName.isEmpty()) {
                Map<String, List<ValidationErrorResponse>> errors = new HashMap<>();
                errors.put(fieldName, List.of(new ValidationErrorResponse(ErrorCode.INVALID_VALUE.name())));
                return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED.name(), errors);
            }
            return build(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED.name());
        }

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
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_ERROR.name());
    }

    private static ErrorResponse build(HttpStatusCode status, String message) {
        return new ErrorResponse(status, String.valueOf(status.value()), message, Collections.emptyMap(), Instant.now().toEpochMilli());
    }

    private static ErrorResponse build(HttpStatusCode status, String message, Map<String, List<ValidationErrorResponse>> errors) {
        return new ErrorResponse(status, String.valueOf(status.value()), message, errors, Instant.now().toEpochMilli());
    }
}
