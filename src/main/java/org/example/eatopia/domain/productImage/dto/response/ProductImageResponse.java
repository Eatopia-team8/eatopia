package org.example.eatopia.domain.productImage.dto.response;

import org.example.eatopia.domain.productImage.entity.ProductImage;

import java.time.LocalDateTime;

public record ProductImageResponse(
        Long id,
        Long productId,
        String imageUrl,
        Integer displayOrder,
        Boolean isThumbnail,
        LocalDateTime createdAt
) {
    public static ProductImageResponse from(ProductImage image) {

        return new ProductImageResponse(
                image.getId(),
                image.getProduct().getId(),
                image.getImageUrl(),
                image.getDisplayOrder(),
                image.getIsThumbnail(),
                image.getCreatedAt()
        );
    }
}
