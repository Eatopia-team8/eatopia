package org.example.eatopia.domain.product.dto.response;

import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        ProductStatus status,
        Long categoryId,
        SellerInfo seller,
        List<ImageInfo> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ProductResponse of(Product product, List<ProductImage> images) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCategory().getId(),
                SellerInfo.from(product.getSeller()),
                images.stream()
                        .map(ImageInfo::from)
                        .toList(),
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

    public record ImageInfo(
            Long id,
            String imageUrl,
            Integer displayOrder,
            Boolean isThumbnail
    ) {
        public static ImageInfo from(ProductImage image) {
            return new ImageInfo(
                    image.getId(),
                    image.getImageUrl(),
                    image.getDisplayOrder(),
                    image.getIsThumbnail()
            );
        }
    }
}
