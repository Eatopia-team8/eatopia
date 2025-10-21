package org.example.eatopia.domain.order.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.dto.response.OrderResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.order.validator.OrderValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    /**
     * Validator에서 orderId, userId 다르면 오류 출력
     */
    @Override
    public OrderDetailResponse getOrder(Long userId, Long orderId) {
        Order order = orderValidator.findByUserIdAndIdOrThrow(userId, orderId);

        return OrderDetailResponse.from(order);
    }

    @Override
    public Page<OrderResponse> getOrders(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserId(userId, pageable);

        return ordersPage.map(OrderResponse::from);
    }

    @Override
    public Order findOrderByIdAndUser(Long userId, Long orderId) {
        return orderValidator.findByUserIdAndIdOrThrow(userId, orderId);
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
}
