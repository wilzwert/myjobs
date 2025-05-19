package com.wilzwert.myjobs.infrastructure.mapper;


import java.util.List;

/**
 * Mapper for entities which can be mapped to domain views (i.e. "partial" objects that are NOT domain aggregates)
 * @author Wilhelm Zwertvaegher
 * Date:13/05/2025
 * Time:17:08
 * V : domain view class
 * E : entity (as in persisted entity) class
 * R : response (DTO) class
 */
public interface EntityViewMapper<V, E, R> {
    R toResponseFromView(V view);

    V toDomainView(E entity);

    List<V> toDomainView(List<E> entity);

    List<R> toResponseFromView(List<V> views);
}
