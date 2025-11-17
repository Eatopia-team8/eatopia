package org.example.eatopia.domain.productImage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.product.entity.Product;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "product_image", indexes = {
        // product_id로 조회 + displayOrder 정렬
        @Index(name = "idx_product_image_product_display",
                columnList = "product_id, display_order ASC"),

        // 썸네일 이미지 빠른 조회
        @Index(name = "idx_product_image_product_thumbnail",
                columnList = "product_id, is_thumbnail"),

        // 이미지 개수 카운트 최적화
        @Index(name = "idx_product_image_product_id",
                columnList = "product_id")
})
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean isThumbnail;

    public static ProductImage create(Product product, String imageUrl, Integer displayOrder, Boolean isThumbnail) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .isThumbnail(isThumbnail)
                .build();
    }

    public void updateThumbnailStatus(Boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }

    public void updateDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
