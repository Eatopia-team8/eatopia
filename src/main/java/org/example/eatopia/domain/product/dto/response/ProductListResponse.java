package org.example.eatopia.domain.product.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast
) {
    public static ProductListResponse from(Page<ProductResponse> page) {
        return new ProductListResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
