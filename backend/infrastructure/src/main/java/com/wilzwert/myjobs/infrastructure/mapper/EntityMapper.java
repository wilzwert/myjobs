package com.wilzwert.myjobs.infrastructure.mapper;

import com.wilzwert.myjobs.core.domain.model.DomainPage;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RestPage;
import org.mapstruct.Mappings;
import org.springframework.data.domain.Page;

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

    default RestPage<S> toResponse(DomainPage<D> domain) {
        return new RestPage<S>(
            toResponse(domain.getContent()),
            domain.getCurrentPage(),
            domain.getPageSize(),
            domain.getTotalElementsCount(),
            domain.getPageCount()
        );
    }


    /* Dans DomainPage <-> Page
     private final List<T> content; List<T> getContent();
    private final int currentPage; int getNumber();
    private final int pageSize; int getSize();
    private final long totalElementsCount; long getTotalElements();
    private final int pageCount; int getTotalPages();
    Dans Page
    int getTotalPages();
    long getTotalElements();
    int getNumber();
    int getSize();
    int getNumberOfElements();
    List<T> getContent();
     */
    default DomainPage<D> toDomain(Page<E> entity) {
        return DomainPage.builder(toDomain(entity.getContent()))
                    .currentPage(entity.getNumber())
                    .pageSize(entity.getSize())
                    .totalElementsCount(entity.getTotalElements())
                .build();
    }
}
