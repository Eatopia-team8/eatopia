package org.example.eatopia.domain.refund.dto.event;

import org.example.eatopia.domain.refund.entity.Refund;

public record RefundSuccessEvent(
        Refund refund
) {
}
