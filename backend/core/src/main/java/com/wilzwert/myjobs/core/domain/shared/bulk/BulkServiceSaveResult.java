package com.wilzwert.myjobs.core.domain.shared.bulk;


/**
 * @author Wilhelm Zwertvaegher
 * Date:15/05/2025
 * Time:12:03
 */

public record BulkServiceSaveResult(int totalCount, int updatedCount, int createdCount, int deletedCount) {}