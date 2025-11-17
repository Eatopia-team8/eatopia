package org.example.eatopia.domain.payment.dto.response;

import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.enums.PaymentMethod;
import org.example.eatopia.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal price,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String impUid,
        String merchantUid,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PaymentResponse from(Payment payment) {

        BigDecimal price = payment.getPrice().setScale(0, RoundingMode.FLOOR);

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                price,
                payment.getMethod(),
                payment.getStatus(),
                payment.getImpUid(),
                payment.getMerchantUid(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
