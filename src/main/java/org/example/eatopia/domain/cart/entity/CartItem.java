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
import org.example.eatopia.domain.product.entity.Product;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected;

    @Builder(access = AccessLevel.PRIVATE)
    private CartItem(Cart cart, Product product, Integer quantity, boolean isSelected) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    public static CartItem create(Cart cart, Product product, Integer quantity) {

        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .isSelected(true)
                .build();
    }

    public void addQuantity(Integer quantity) {
        this.quantity += quantity;
    }

    public void updateQuantity(QuantityChangeType opration) {

        int newQuantity = opration.apply(this.getQuantity());

        if (newQuantity < 1) {
            throw new GlobalException(CartErrorCode.CANNOT_DECREMENT);
        }

        this.quantity = newQuantity;
    }

    public void updateIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
