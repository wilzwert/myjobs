package com.wilzwert.myjobs.core.domain.shared.bulk;


/**
 * @author Wilhelm Zwertvaegher
 */

public record BulkDataSaveResult(int totalCount, int updatedCount, int createdCount, int deletedCount) {}