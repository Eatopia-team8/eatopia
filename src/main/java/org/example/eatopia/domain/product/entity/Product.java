package org.example.eatopia.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;

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
                       BigDecimal price, Long stock, ProductStatus status, Category category) {

        if (name != null) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (thumbnailUrl != null) {
            this.thumbnailUrl = thumbnailUrl;
        }
        if (price != null) {
            this.price = price;
        }
        if (stock != null) {
            this.stock = stock;
        }
        if (status != null) {
            this.status = status;
        }
        if (category != null) {
            this.category = category;
        }
    }
}