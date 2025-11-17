package org.example.eatopia.domain.order.dto.response;

import org.example.eatopia.domain.delivery.dto.response.DeliveryResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 주문 단건 상세 조회를 위한 DTO
 */
public record OrderDetailResponse(
        Long orderId,
        Long userId,
        String orderCode,
        OrderStatus orderStatus,
        BigDecimal totalProductPrice,
        BigDecimal discountProductPrice,
        BigDecimal totalDeliveryPrice,
        BigDecimal discountDeliveryPrice,
        BigDecimal finalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String address,
        List<OrderDetailProductResponse> orderProduct,
        DeliveryResponse delivery
) {
    public static OrderDetailResponse from(final Order order) {
        List<OrderDetailProductResponse> itemResponses = order.getOrderDetails().stream()
                .map(OrderDetailProductResponse::from)
                .collect(Collectors.toList());

        return new OrderDetailResponse(
                order.getId(),
                order.getUser().getId(),
                order.getCode(),
                order.getStatus(),
                order.getTotalProductPrice(),
                order.getDiscountProductPrice(),
                order.getTotalDeliveryPrice(),
                order.getDiscountDeliveryPrice(),
                order.getFinalPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getAddress(),
                itemResponses,
                DeliveryResponse.from(order.getDelivery())
        );
    }
}