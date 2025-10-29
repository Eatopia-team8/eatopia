package org.example.eatopia.domain.payment.dto.request;

import org.example.eatopia.domain.payment.enums.PaymentMethod;

public record PaymentCreateRequest(
        Long orderId,
        PaymentMethod paymentMethod
) {
}
