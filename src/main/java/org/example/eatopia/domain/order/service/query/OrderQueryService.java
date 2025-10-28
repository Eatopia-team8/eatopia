package org.example.eatopia.domain.order.service.query;

import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.dto.response.OrderResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryService {
    OrderDetailResponse getOrder(Long userId, Long orderId);

    Page<OrderResponse> getOrders(Long userId, Pageable pageable);

    Order findOrderByUserAndId(Long userId, Long orderId);

    boolean isFirstOrder(Long userId);

    Order findOrderByCode(String code);

    OrderDetail getOrderDetailByUserId(Long orderDetailId, Long userId);
}
