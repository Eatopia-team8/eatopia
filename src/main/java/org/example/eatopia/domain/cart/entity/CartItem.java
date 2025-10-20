package org.example.eatopia.domain.cart.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.enums.QuantityChangeType;
import org.example.eatopia.domain.cart.exception.CartErrorCode;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected;

    @Builder(access = AccessLevel.PRIVATE)
    private CartItem(Cart cart, Long productId, int quantity, boolean isSelected) {
        this.cart = cart;
        this.productId = productId;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    public static CartItem create(Cart cart, Long productId, int quantity) {

        return CartItem.builder()
                .cart(cart)
                .productId(productId)
                .quantity(quantity)
                .isSelected(true)
                .build();
    }

    public void updateQuantity(QuantityChangeType opration) {

        int newQuantity = opration.apply(this.getQuantity());

        if (newQuantity < 1) {
            throw new GlobalException(CartErrorCode.CANNOT_DECREMENT);
        }

        this.quantity = newQuantity;
    }
}
