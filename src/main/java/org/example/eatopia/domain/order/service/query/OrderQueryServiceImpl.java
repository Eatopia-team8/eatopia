package org.example.eatopia.domain.order.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.dto.response.OrderResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.repository.OrderDetailRepository;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    /**
     * Validator에서 orderId, userId 다르면 오류 출력
     */
    @Override
    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));

        return OrderDetailResponse.from(order);
    }

    @Override
    public Page<OrderResponse> getOrders(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);

        return ordersPage.map(OrderResponse::from);
    }

    @Override
    public Order findOrderByUserAndId(Long userId, Long orderId) {

        return orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    /**
     * 사용자의 첫 주문 여부를 확인합니다.
     *
     * @param userId 확인할 사용자의 ID
     * @return 첫 주문일 경우 true, 아닐 경우 false
     */
    @Override
    public boolean isFirstOrder(Long userId) {
        return !orderRepository.existsByUserId(userId);
    }

    @Override
    public Order findOrderByCode(String code) {
        return orderRepository.findByCode(code)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    public OrderDetail getOrderDetailByUserId(Long orderDetailId, Long userId) {
        return orderDetailRepository.findByIdAndOrderUserId(orderDetailId, userId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
