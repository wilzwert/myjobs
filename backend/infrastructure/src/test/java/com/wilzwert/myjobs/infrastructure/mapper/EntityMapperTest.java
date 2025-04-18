package com.wilzwert.myjobs.infrastructure.mapper;

import com.wilzwert.myjobs.core.domain.model.DomainPage;
import com.wilzwert.myjobs.infrastructure.api.rest.dto.RestPage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMapperTest {

    EntityMapper<String, Integer, Object, Object, Object, Object, String> mapper =
        new EntityMapper<>() {
            @Override public Integer toEntity(String domain) { return Integer.parseInt(domain); }

            @Override public List<Integer> toEntity(List<String> domains) {
                return domains.stream().map(Integer::parseInt).toList();
            }
            @Override public String toDomain(Integer entity) { return String.valueOf(entity); }

            @Override public List<String> toDomain(List<Integer> entities) {
                return entities.stream().map(String::valueOf).toList();
            }
            @Override public String toResponse(String domain) { return domain.toUpperCase(); }

            @Override public List<String> toResponse(List<String> domains) {
                return domains.stream().map(String::toUpperCase).toList();
            }

            @Override public Object toCommand(Object request) { return null; }

            @Override public Object toUpdateCommand(Object request) { return null; }
        };

    @Test
    void toResponse_shouldMapDomainPageToRestPage() {
        DomainPage<String> domainPage = DomainPage.builder(List.of("foo", "bar"))
                .currentPage(1)
                .pageSize(2)
                .totalElementsCount(4)
                .build();

        RestPage<String> restPage = mapper.toResponse(domainPage);

        assertThat(restPage.getContent()).containsExactly("FOO", "BAR");
        assertEquals(1, restPage.getCurrentPage());
        assertEquals(2, restPage.getPageSize());
        assertEquals(4, restPage.getTotalElementsCount());
        assertEquals(2, restPage.getPagesCount());
    }

    @Test
    void toDomain_shouldMapPageToDomainPage() {
        List<Integer> content = List.of(1, 2);
        PageRequest pageable = PageRequest.of(1, 2);
        Page<Integer> entityPage = new PageImpl<>(content, pageable, 4);

        DomainPage<String> domainPage = mapper.toDomain(entityPage);

        assertThat(domainPage.getContent()).containsExactly("1", "2");
        assertEquals(1, domainPage.getCurrentPage());
        assertEquals(2, domainPage.getPageSize());
        assertEquals(4, domainPage.getTotalElementsCount());
        assertEquals(2, domainPage.getPageCount());
    }
}
