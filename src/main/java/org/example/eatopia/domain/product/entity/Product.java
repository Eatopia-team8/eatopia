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
@Table(name = "product", indexes = {
        // 카테고리별 최신순 조회 (V3 캐시의 핵심 쿼리)
        @Index(name = "idx_product_category_created", columnList = "category_id, created_at DESC"),

        // 가격 정렬 조회
        @Index(name = "idx_product_category_price", columnList = "category_id, price"),

        // 상태 필터링
        @Index(name = "idx_product_status", columnList = "status"),

        // 판매자별 상품 조회
        @Index(name = "idx_product_seller", columnList = "seller_id"),

        // 키워드 검색용 (name)
        @Index(name = "idx_product_name", columnList = "name"),

        // 복합 인덱스: 상태 제외 + 카테고리 + 생성일
        @Index(name = "idx_product_status_category_created",
                columnList = "status, category_id, created_at DESC")
})
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, scale = 0)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    public static Product create(String name, String description, BigDecimal price,
                                 Integer stock, ProductStatus status, Category category, User seller) {

        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .status(status)
                .category(category)
                .seller(seller)
                .build();
    }

    public void update(String name, String description, BigDecimal price,
                       Integer stock, ProductStatus status, Category category) {

        Optional.ofNullable(name).ifPresent(value -> this.name = value);
        Optional.ofNullable(description).ifPresent(value -> this.description = value);
        Optional.ofNullable(price).ifPresent(value -> this.price = value);
        Optional.ofNullable(stock).ifPresent(value -> this.stock = value);
        Optional.ofNullable(status).ifPresent(value -> this.status = value);
        Optional.ofNullable(category).ifPresent(value -> this.category = value);
    }

    public void verifySeller(Long userId) {
        if (!this.seller.getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.PRD_NO_PERMISSION);
        }
    }

    public void verifySellerOrAdmin(Long userId, boolean isAdmin) {
        if (!isAdmin && !this.seller.getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.PRD_NO_PERMISSION);
        }
    }

    public void decreaseStock(Integer quantity) {
        validateQuantity(quantity);

        // 재고 체크
        if (this.stock < quantity) {
            throw new ProductException(ProductErrorCode.PRD_OUT_OF_STOCK);
        }

        this.stock -= quantity;
    }

    public void increaseStock(Integer quantity) {
        validateQuantity(quantity);

        this.stock += quantity;
    }

    private void validateQuantity(Integer quantity) {
        // 수량 체크
        if (quantity == null || quantity <= 0) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_ORDER_QUANTITY);
        }
    }
}