package com.wilzwert.myjobs.core.domain.model.jsonld;


/**
 * @author Wilhelm Zwertvaegher
 * Date:04/04/2025
 * Time:13:33
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
