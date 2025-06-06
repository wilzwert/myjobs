package com.wilzwert.myjobs.infrastructure.api.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

/**
 * @author Wilhelm Zwertvaegher
 */

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