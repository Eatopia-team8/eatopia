package org.example.eatopia.domain.payment.dto.response;

import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.entity.PaymentMethod;
import org.example.eatopia.domain.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long orderId,
        BigDecimal price,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String impUid,
        String merchantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPrice(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getImpUid(),
                payment.getMerchantUid(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
