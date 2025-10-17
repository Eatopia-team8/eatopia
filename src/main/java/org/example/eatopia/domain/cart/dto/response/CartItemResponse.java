package org.example.eatopia.domain.cart.dto.response;

import org.example.eatopia.domain.cart.entity.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal totalPrice,
        boolean isSelected,
        LocalDateTime createdAt
) {
    public static CartItemResponse of(CartItem cartItem, String name) {

        BigDecimal price = cartItem.getPrice();
        BigDecimal decimalQuantity = BigDecimal.valueOf(cartItem.getQuantity());

        return new CartItemResponse(
                cartItem.getProductId(),
                name,
                cartItem.getPrice(),
                cartItem.getQuantity(),
                price.multiply(decimalQuantity),
                cartItem.isSelected(),
                cartItem.getCreatedAt()
        );
    }
}
