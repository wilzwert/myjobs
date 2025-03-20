package com.wilzwert.myjobs.infrastructure.api.rest.dto;

/**
 * @author Wilhelm Zwertvaegher
 * Date:20/03/2025
 * Time:14:36
 */
import com.wilzwert.myjobs.core.domain.model.DomainPage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO @ApiModel(value = "Page", description = "Paginated content")
@Data
@AllArgsConstructor
public class RestPage<T> {
    // @ApiModelProperty(value = "Page content")
    private final List<T> content;

    // @ApiModelProperty(value = "Current page (starts at 0)", required = true)
    private final int currentPage;

    // @ApiModelProperty(value = "Number of elements on each page", required = true)
    private final int pageSize;

    // @ApiModelProperty(value = "Total number of elements to paginate", required = true)
    private final long totalElementsCount;

    // @ApiModelProperty(value = "Number of resulting pages", required = true)
    private final int pagesCount;
}