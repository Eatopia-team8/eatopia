package org.example.eatopia.domain.delivery.dto.response;

import org.example.eatopia.domain.delivery.entity.Delivery;
import org.example.eatopia.domain.delivery.enums.DeliveryStatus;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long deliveryId,
        DeliveryStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DeliveryResponse from(Delivery delivery) {
        //주문이 완료되지 않으면 NULL 반환
        if (delivery == null) {
            return null;
        }

        return new DeliveryResponse(
                delivery.getId(),
                delivery.getStatus(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }
}