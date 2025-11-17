package org.example.eatopia.domain.payment.dto.event;

public record PaymentCompletedEvent(
        Long orderId,
        Long userId
) {
}
