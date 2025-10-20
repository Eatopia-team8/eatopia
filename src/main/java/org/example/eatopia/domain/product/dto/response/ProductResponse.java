package org.example.eatopia.domain.product.dto.response;

import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String thumbnailUrl,
        BigDecimal price,
        Long stock,
        ProductStatus status,
        Long categoryId,
        SellerInfo seller,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse from(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getThumbnailUrl(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCategory().getId(),
                SellerInfo.from(product.getSeller()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public record SellerInfo(
            Long id,
            String name,
            String company,
            String email
    ) {

        public static SellerInfo from(User user) {
            
            return new SellerInfo(
                    user.getId(),
                    user.getName(),
                    user.getCompany(),
                    user.getEmail()
            );
        }
    }
}