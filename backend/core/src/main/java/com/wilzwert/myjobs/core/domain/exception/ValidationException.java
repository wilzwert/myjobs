package com.wilzwert.myjobs.core.domain.exception;

import com.wilzwert.myjobs.core.domain.shared.validation.ErrorCode;
import com.wilzwert.myjobs.core.domain.shared.validation.ValidationError;

import java.util.List;
import java.util.Map;

/**
 * @author Wilhelm Zwertvaegher
 * Date:25/07/2025
 * Time:12:10
 */

public class ValidationException extends DomainException {
  private final Map<String, List<ValidationError>> errors;

  public ValidationException(Map<String, List<ValidationError>> errors) {
    super(ErrorCode.VALIDATION_FAILED);
    this.errors = errors;
  }

  public Map<String, List<ValidationError>> getErrors() {
    return errors;
  }

  public List<ValidationError> getFlatErrors() {
    return errors.values().stream()
            .flatMap(List::stream)
            .toList();
  }
}


