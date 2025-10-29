package org.example.eatopia.domain.product.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> products,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize
) {
    public static ProductListResponse of(List<ProductResponse> products, Page<?> page) {
        return new ProductListResponse(
                products,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}