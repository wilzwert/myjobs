package com.wilzwert.myjobs.infrastructure.mapper;

public class MapperMissingImplementation extends RuntimeException {
    public MapperMissingImplementation(String message) {
        super(message);
    }
}