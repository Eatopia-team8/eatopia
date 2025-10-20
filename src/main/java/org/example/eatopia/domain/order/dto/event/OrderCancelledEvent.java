package org.example.eatopia.domain.order.dto.event;

import org.example.eatopia.domain.order.entity.Order;

public record OrderCancelledEvent(
        Order order
) {
}
