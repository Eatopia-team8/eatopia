package org.example.eatopia.domain.product.dto.request;

import org.example.eatopia.domain.product.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        String name,
        String description,
        String thumbnailUrl,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        Long categoryId
) {

    public boolean hasNoUpdate() {

        return name == null && description == null && thumbnailUrl == null
                && price == null && stock == null && status == null && categoryId == null;
    }
}