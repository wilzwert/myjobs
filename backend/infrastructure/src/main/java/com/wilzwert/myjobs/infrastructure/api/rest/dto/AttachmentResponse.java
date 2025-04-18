package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:39
 */

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentResponse {
    private UUID id;
    private String name;
    private String filename;
    private String contentType;
    private Instant createdAt;
}