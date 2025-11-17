package org.example.eatopia.domain.order.service.command;

import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.settlement.entity.Settlement;

import java.util.List;

public interface OrderCommandService {
    OrderDetailResponse createOrder(Long userId, OrderCreateRequest request);

    OrderDetailResponse cancelOrder(Long userId, Long orderId);

    OrderDetailResponse successOrder(Long userId, Long orderId);

    void settlementToOrderDetails(List<Long> orderDetailIds, Settlement settlement);

    void rollbackSettlementForOrderDetails(List<OrderDetail> orderDetails);
}
