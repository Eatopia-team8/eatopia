package org.example.eatopia.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PaymentVerifyRequest(
        @NotBlank(message = "PortOne 거래 ID는 필수입니다.")
        String impUid,

        @NotBlank(message = "주문 번호는 필수입니다.")
        String merchantUid
) {
}