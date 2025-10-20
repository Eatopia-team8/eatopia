package org.example.eatopia.domain.payment.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final OrderRepository orderRepository;

    public Order paymentCreateValidate(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));

        return order;
    }
}
