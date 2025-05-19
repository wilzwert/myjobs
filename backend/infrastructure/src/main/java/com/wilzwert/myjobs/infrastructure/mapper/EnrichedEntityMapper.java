package com.wilzwert.myjobs.infrastructure.mapper;

import com.wilzwert.myjobs.core.domain.model.pagination.DomainPage;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RestPage;

import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 * Date:14/03/2025
 * Time:22:46
 X for the enriched domain class
 */
public interface EnrichedEntityMapper<D, E, R, C, A, U, S, X> extends EntityMapper<D, E, R, C, A, U, S> {

    default S toEnrichedResponse(X extended) {
        throw new MapperMissingImplementation("enriched entity mapping must be implemented in actual mapper before use");
    }

    default  List<S> toEnrichedResponse(List<X> extended) {
        return extended.stream().map(this::toEnrichedResponse).toList();
    }

    default RestPage<S> toEnrichedResponse(DomainPage<X> domain) {
        return new RestPage<>(
                toEnrichedResponse(domain.getContent()),
                domain.getCurrentPage(),
                domain.getPageSize(),
                domain.getTotalElementsCount(),
                domain.getPageCount()
        );
    }
}
