package org.example.eatopia.domain.order.dto.response;

import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.product.entity.Product;

import java.math.BigDecimal;

public record OrderDetailProductResponse(
        Long orderDetailId,
        Long ProductId,
        String productName,
        Long sellerId,
        Long quantity,
        BigDecimal price
) {
    public static OrderDetailProductResponse from(final OrderDetail orderDetail) {

        Product product = orderDetail.getProduct();

        return new OrderDetailProductResponse(
                orderDetail.getId(),
                product.getId(),
                product.getName(),
                orderDetail.getSellerId(),
                orderDetail.getQuantity(),
                orderDetail.getPrice()
        );
    }
}
