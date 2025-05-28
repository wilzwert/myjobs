package com.wilzwert.myjobs.core.domain.model;

public interface EntityId<T> {
    T value();

    @Override
    String toString();
}
