package org.example.eatopia.domain.cart.dto.response;

import org.example.eatopia.domain.cart.entity.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        BigDecimal deliveryFee,
        BigDecimal finalAmount
) {

    public static CartResponse of(
            Cart cart,
            List<CartItemResponse> items,
            BigDecimal totalAmount,
            BigDecimal deliveryFee,
            BigDecimal finalAmount
    ) {

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                items,
                totalAmount,
                deliveryFee,
                finalAmount
        );
    }
}
