package org.example.eatopia.domain.refund.dto.response;

import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundReason;
import org.example.eatopia.domain.refund.enums.RefundStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public record RefundResponse(
        Long refundId,
        Long orderDetailId,
        Long paymentId,
        BigDecimal price,
        Integer quantity,
        BigDecimal amount,
        RefundReason reason,
        RefundStatus status,
        LocalDateTime createdAt
) {
    public static RefundResponse from(Refund refund) {

        BigDecimal amount = refund.getAmount().setScale(0, RoundingMode.FLOOR);

        return new RefundResponse(
                refund.getId(),
                refund.getOrderDetail().getId(),
                refund.getPayment().getId(),
                refund.getPrice(),
                refund.getQuantity(),
                amount,
                refund.getReason(),
                refund.getStatus(),
                refund.getCreatedAt()
        );
    }
}