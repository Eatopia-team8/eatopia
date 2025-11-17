package org.example.eatopia.domain.refund.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.refund.enums.RefundReason;

public record RefundCreateRequest(
        @NotNull(message = "주문 번호는 필수입니다.")
        Long orderDetailId,
        @NotNull(message = "환불 사유는 필수입니다.")
        RefundReason reason,
        @NotNull(message = "환불 수량은 필수입니다.")
        @Min(value = 1, message = "환불 수량은 1 이상이어야 합니다.")
        Integer quantity
) {
}