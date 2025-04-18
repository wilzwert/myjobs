package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wilzwert.myjobs.core.domain.exception.EntityAlreadyExistsException;
import com.wilzwert.myjobs.core.domain.exception.EntityNotFoundException;
import com.wilzwert.myjobs.core.domain.exception.LoginException;
import com.wilzwert.myjobs.core.domain.exception.PasswordMatchException;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    @JsonIgnore
    private HttpStatusCode httpStatusCode;

    private String status;
    private String message;
    private String time;

    public static <E extends EntityAlreadyExistsException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    public static <E extends EntityNotFoundException>  ErrorResponse fromException(E ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    public static ErrorResponse fromException(ResponseStatusException ex) {
        return build(ex.getStatusCode(), ex.getReason());
    }

    public static ErrorResponse fromException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errors = new StringBuilder();
        for(FieldError fieldError : fieldErrors){
            errors.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append(". ");
        }
        return build(ex.getStatusCode(), errors.toString());
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

    public static ErrorResponse fromException(HttpMediaTypeException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    public static ErrorResponse fromException(HttpClientErrorException ex) {
        return build(ex.getStatusCode(), ex.getMessage());
    }

    public static ErrorResponse fromException(PasswordMatchException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public static ErrorResponse fromException(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred.");
    }

    private static ErrorResponse build(HttpStatusCode status, String message) {
        return new ErrorResponse(status, String.valueOf(status.value()), message, new Date().toString());
    }


}
