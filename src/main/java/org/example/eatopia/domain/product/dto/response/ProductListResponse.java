package org.example.eatopia.domain.product.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> content,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize
) {
    public static ProductListResponse of(List<ProductResponse> content, Page<?> page) {
        return new ProductListResponse(
                content,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}