package org.example.eatopia.domain.product.dto.request;

import java.math.BigDecimal;

public record ProductSearchCondition(
        String keyword,
        Long categoryId,
        String status,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
