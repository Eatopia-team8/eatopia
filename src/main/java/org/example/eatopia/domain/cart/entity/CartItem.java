package org.example.eatopia.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cartId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(precision = 10, nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean isSelected;

    @Builder(access = AccessLevel.PRIVATE)
    private CartItem(Cart cartId, Long productId, int quantity, BigDecimal price, boolean isSelected) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.isSelected = isSelected;
    }

    public static CartItem create(Cart cart, Long productId, int quantity, BigDecimal price) {
        return CartItem.builder()
                .cartId(cart)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .isSelected(true)
                .build();
    }
}
