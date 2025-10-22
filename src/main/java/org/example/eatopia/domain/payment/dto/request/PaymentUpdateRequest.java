package org.example.eatopia.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.payment.entity.PaymentMethod;

public record PaymentUpdateRequest(
        @NotNull
        PaymentMethod paymentMethod
) {
}
