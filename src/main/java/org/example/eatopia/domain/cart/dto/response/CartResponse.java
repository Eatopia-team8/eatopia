package org.example.eatopia.domain.cart.dto.response;

import org.example.eatopia.domain.cart.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal finalAmount
) {
    public static CartResponse of(
            Cart cart,
            List<CartItemResponse> items,
            BigDecimal totalAmount,
            BigDecimal discountAmount,
            BigDecimal finalAmount
    ) {
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                items,
                totalAmount,
                discountAmount,
                finalAmount
        );
    }
}
