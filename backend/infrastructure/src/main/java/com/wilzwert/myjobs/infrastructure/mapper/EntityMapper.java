package com.wilzwert.myjobs.infrastructure.mapper;

import com.wilzwert.myjobs.core.domain.shared.pagination.DomainPage;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RestPage;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:22:46
 * D for Domain aggregate / entity
 * E for persisted Entity
 * R for creation Request DTO
 * C for domain creation command
 * A for update Request DTO
 * U for domain update command
 * S for response
 */
public interface EntityMapper<D, E, R, C, A, U, S> {
    E toEntity(D domain);

    List<E> toEntity(List<D> domains);

    D toDomain(E entity);

    List<D> toDomain(List<E> entities);

    S toResponse(D domain);

    List<S> toResponse(List<D> domains);

    C toCommand(R request);

    U toUpdateCommand(A request);

    default RestPage<S> toResponse(DomainPage<D> domain) {
        return new RestPage<>(
            toResponse(domain.getContent()),
            domain.getCurrentPage(),
            domain.getPageSize(),
            domain.getTotalElementsCount(),
            domain.getPageCount()
        );
    }
    default DomainPage<D> toDomain(Page<E> entity) {
        return DomainPage.builder(toDomain(entity.getContent()))
                    .currentPage(entity.getNumber())
                    .pageSize(entity.getSize())
                    .totalElementsCount(entity.getTotalElements())
                .build();
    }

}
