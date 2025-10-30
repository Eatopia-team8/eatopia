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
