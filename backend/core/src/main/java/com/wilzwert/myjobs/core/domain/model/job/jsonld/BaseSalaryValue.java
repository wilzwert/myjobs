package com.wilzwert.myjobs.core.domain.model.job.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 */
public record BaseSalaryValue(
    String type,
    String currency,
    String minValue,
    String maxValue,
    String value,
    String unitText,
    String minPrice,
    String maxPrice,
    String price,
    String priceCurrency
    ) {
}
