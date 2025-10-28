package org.example.eatopia.domain.order.service.query;

import org.example.eatopia.domain.order.entity.OrderDetail;

public interface OrderDetailQueryService {
    OrderDetail getOrderDetailEntityById(Long orderDetailId);
}
