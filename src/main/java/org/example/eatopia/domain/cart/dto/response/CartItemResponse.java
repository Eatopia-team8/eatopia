package org.example.eatopia.domain.cart.dto.response;

import org.example.eatopia.domain.cart.entity.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long productId,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalPrice,
        boolean isSelected,
        LocalDateTime createdAt
) {
    public static CartItemResponse of(CartItem cartItem, String name, BigDecimal price) {

        BigDecimal decimalQuantity = BigDecimal.valueOf(cartItem.getQuantity());

        return new CartItemResponse(
                cartItem.getProduct().getId(),
                name,
                price,
                cartItem.getQuantity(),
                price.multiply(decimalQuantity),
                cartItem.isSelected(),
                cartItem.getCreatedAt()
        );
    }
}
