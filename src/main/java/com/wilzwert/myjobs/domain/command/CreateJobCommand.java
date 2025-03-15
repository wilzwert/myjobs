package com.wilzwert.myjobs.domain.command;

import java.util.UUID;

/**
 * @author Wilhelm Zwertvaegher
 * Date:13/03/2025
 * Time:11:10
 */

public record CreateJobCommand(String title, String url, String description, String profile, UUID userId) {
}
