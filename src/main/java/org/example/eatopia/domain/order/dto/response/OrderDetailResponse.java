package org.example.eatopia.domain.order.dto.response;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 주문 단건 상세 조회를 위한 DTO
 */
public record OrderDetailResponse(
        Long orderId,
        Long userId,
        Long productId,
        Long sellerId,
        String orderCode,
        OrderStatus orderStatus,
        BigDecimal totalProductPrice,
        BigDecimal discountProductPrice,
        BigDecimal totalDeliveryPrice,
        BigDecimal discountDeliveryPrice,
        BigDecimal finalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderDetailResponse from(final Order order) {
        return new OrderDetailResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getSellerId(),
                order.getCode(),
                order.getStatus(),
                order.getTotalProductPrice(),
                order.getDiscountProductPrice(),
                order.getTotalDeliveryPrice(),
                order.getDiscountDeliveryPrice(),
                order.getFinalPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}