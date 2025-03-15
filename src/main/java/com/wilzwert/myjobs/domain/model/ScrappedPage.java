package com.wilzwert.myjobs.domain.model;


import java.time.LocalDateTime;

/**
 * @author Wilhelm Zwertvaegher
 * Date:12/03/2025
 * Time:17:19
 */

public record ScrappedPage(String title, String description, String salary, LocalDateTime createdAt, LocalDateTime validThrough, String employmentType, String organization, String city) {
}
