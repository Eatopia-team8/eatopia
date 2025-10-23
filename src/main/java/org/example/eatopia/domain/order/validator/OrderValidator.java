package org.example.eatopia.domain.order.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderStatus;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    public void orderSuccessValidate(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new GlobalException(OrderErrorCode.CANNOT_SUCCESS_ORDER);
        }
    }

    public void orderCancelValidate(Order order) {
        if (order.getStatus() != OrderStatus.SUCCESS) {
            throw new GlobalException(OrderErrorCode.CANNOT_CANCEL_ORDER);
        }
    }

    public void validateStock(Product product, Long quantity) {
        if (product.getStock() < quantity) {
            throw new GlobalException(OrderErrorCode.OUT_OF_STOCK);
        }
    }
}
