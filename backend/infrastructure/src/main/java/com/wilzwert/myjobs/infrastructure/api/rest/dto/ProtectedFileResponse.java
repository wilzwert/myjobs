package com.wilzwert.myjobs.infrastructure.api.rest.dto;


import lombok.Data;

import java.time.Instant;

/**
 * @author Wilhelm Zwertvaegher
 * Date:27/05/2025
 * Time:16:37
 */
@Data
public class ProtectedFileResponse {

    private String fileId;

    private String url;
}
