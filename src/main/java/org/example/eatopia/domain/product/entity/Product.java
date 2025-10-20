package org.example.eatopia.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Column(nullable = false)
    private Long stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, scale = 0)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    public static Product create(String name, String description, String thumbnailUrl,
                                 BigDecimal price, Long stock, ProductStatus status,
                                 Category category, User seller) {

        return Product.builder()
                .name(name)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .price(price)
                .stock(stock)
                .status(status)
                .category(category)
                .seller(seller)
                .build();
    }

    public void update(String name, String description, String thumbnailUrl,
                       BigDecimal price, Long stock, ProductStatus status,
                       Category category) {

        Optional.ofNullable(name).ifPresent(value -> this.name = value);
        Optional.ofNullable(description).ifPresent(value -> this.description = value);
        Optional.ofNullable(thumbnailUrl).ifPresent(value -> this.thumbnailUrl = value);
        Optional.ofNullable(price).ifPresent(value -> this.price = value);
        Optional.ofNullable(stock).ifPresent(value -> this.stock = value);
        Optional.ofNullable(status).ifPresent(value -> this.status = value);
        Optional.ofNullable(category).ifPresent(value -> this.category = value);
    }

    public void verifySeller(Long userId) {
        if (!this.seller.getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.PRD_UPDATE_NO_PERMISSION);
        }
    }
}