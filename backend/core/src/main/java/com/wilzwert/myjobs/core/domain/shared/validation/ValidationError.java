package com.wilzwert.myjobs.core.domain.shared.validation;


/**
 * @author Wilhelm Zwertvaegher
 * Date:25/04/2025
 * Time:12:07
 */
public record ValidationError(String field, ErrorCode code) {
}
