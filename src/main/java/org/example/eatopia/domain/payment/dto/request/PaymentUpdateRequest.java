package org.example.eatopia.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.payment.enums.PaymentMethod;

public record PaymentUpdateRequest(
        @NotNull
        PaymentMethod paymentMethod
) {
}
