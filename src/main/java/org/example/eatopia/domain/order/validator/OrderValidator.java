package org.example.eatopia.domain.order.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.exception.OrderException;
import org.example.eatopia.domain.product.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    public void orderSuccessValidate(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderException(OrderErrorCode.CANNOT_SUCCESS_ORDER);
        }
    }

    public void orderCancelValidate(Order order) {
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderException(OrderErrorCode.ALREADY_CANCELED_ORDER);
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.SUCCESS) {
            throw new OrderException(OrderErrorCode.CANNOT_CANCEL_ORDER);
        }
    }

    public void validateStock(Product product, Integer quantity) {
        if (product.getStock() < quantity) {
            throw new OrderException(OrderErrorCode.OUT_OF_STOCK);
        }
    }

    public void validateCartItems(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new OrderException(OrderErrorCode.EMPTY_CART_ORDER);
        }
    }

    public void validateFinalPrice(BigDecimal finalPrice) {
        if (finalPrice == null || finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new OrderException(OrderErrorCode.INVALID_FINAL_PRICE);
        }
    }
}
