package com.wilzwert.myjobs.infrastructure.api.mapper;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:22:46
 * D for Domain POJO
 * E for persisted Entity
 * R for creation Request DTO
 * C for domain creation command
 * UR for update Request DTO
 * U for domain update command
 * DC for domain deletion command
 * S for response
 */
public interface EntityMapper<D, E, R, C, UR, U, S> {
    E toEntity(D domain);

    List<E> toEntity(List<D> domains);

    D toDomain(E entity);

    List<D> toDomain(List<E> entities);

    S toResponse(D domain);

    List<S> toResponse(List<D> domains);

    C toCommand(R request);

    U toUpdateCommand(UR request);
}
