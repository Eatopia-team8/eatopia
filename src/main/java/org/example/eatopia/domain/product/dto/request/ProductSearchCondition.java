package org.example.eatopia.domain.product.dto.request;

import org.example.eatopia.domain.product.enums.ProductSortBy;

import java.math.BigDecimal;

public record ProductSearchCondition(
        String keyword,
        Long categoryId,
        String status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductSortBy sortBy
) {
}
