package com.wilzwert.myjobs.infrastructure.mapper;


/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:22:46
 * D for Domain POJO
 * E for persisted Entity
 * R for creation Request DTO
 * C for domain creation command
 * S for response
 */
public interface EntityMapper<D, E, R, C, S> {
    E toEntity(D domain);

    D toDomain(E entity);

    S toResponse(D domain);

    C toCommand(R request);
}
