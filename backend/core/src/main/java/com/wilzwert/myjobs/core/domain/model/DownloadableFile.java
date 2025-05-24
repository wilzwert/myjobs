package com.wilzwert.myjobs.core.domain.model;


/**
 * @author Wilhelm Zwertvaegher
 */
public record DownloadableFile(String fileId, String path, String contentType, String filename) {
}
