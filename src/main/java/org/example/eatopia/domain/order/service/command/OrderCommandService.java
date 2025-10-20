package org.example.eatopia.domain.order.service.command;

import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;

public interface OrderCommandService {
    OrderDetailResponse createOrder(Long userId, OrderCreateRequest request);

    OrderDetailResponse cancelOrder(Long userId, Long orderId);

    OrderDetailResponse successOrder(Long userId, Long orderId);
}
