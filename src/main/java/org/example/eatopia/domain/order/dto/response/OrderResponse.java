package org.example.eatopia.domain.order.dto.response;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 주문 목록 조회를 위한 DTO
 */
public record OrderResponse(
        Long orderId,
        String orderCode,
        OrderStatus orderStatus,
        BigDecimal finalPrice,
        LocalDateTime createdAt
) {
    public static OrderResponse from(Order order) {

        BigDecimal finalPrice = order.getFinalPrice().setScale(0, RoundingMode.FLOOR);

        return new OrderResponse(
                order.getId(),
                order.getCode(),
                order.getStatus(),
                finalPrice,
                order.getCreatedAt()
        );
    }
}
