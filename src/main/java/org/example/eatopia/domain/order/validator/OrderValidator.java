package org.example.eatopia.domain.order.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderStatus;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {
    private final OrderRepository orderRepository;

    public void orderCreateValidate(OrderCreateRequest request) {
        if (request.productId() == null) {
            throw new GlobalException(OrderErrorCode.PRODUCT_ID_REQUIRED);
        }

        if (request.sellerId() == null) {
            throw new GlobalException(OrderErrorCode.SELLER_ID_REQUIRED);
        }
    }

    public void orderSuccessValidate(Order order) {
        validateOrderStatusIsPending(order, OrderErrorCode.CANNOT_SUCCESS_ORDER);
    }

    public void orderCancelValidate(Order order) {
        validateOrderStatusIsPending(order, OrderErrorCode.CANNOT_CANCEL_ORDER);
    }

    private void validateOrderStatusIsPending(Order order, OrderErrorCode errorCode) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new GlobalException(errorCode);
        }
    }

    public Order findByIdAndUserIdOrThrow(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
