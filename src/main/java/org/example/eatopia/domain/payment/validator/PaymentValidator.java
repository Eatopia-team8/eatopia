package org.example.eatopia.domain.payment.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final OrderQueryService orderQueryService;

    public Order paymentCreateValidate(Long userId, Long orderId) {
        Order order = orderQueryService.findOrderById(orderId);

        if (!order.getUserId().equals(userId)) {
            throw new GlobalException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        return order;
    }
}
