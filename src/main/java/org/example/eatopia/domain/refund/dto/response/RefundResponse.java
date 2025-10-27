package org.example.eatopia.domain.refund.dto.response;

import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundReason;
import org.example.eatopia.domain.refund.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundResponse(
        Long refundId,
        Long orderDetailId,
        Long paymentId,
        BigDecimal amount,
        RefundReason reason,
        RefundStatus status,
        LocalDateTime createdAt
) {
    public static RefundResponse from(Refund refund) {
        return new RefundResponse(
                refund.getId(),
                refund.getOrderDetail().getId(),
                refund.getPayment().getId(),
                refund.getAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getCreatedAt()
        );
    }
}