package org.example.eatopia.domain.delivery.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.delivery.enums.DeliveryStatus;

public record DeliveryUpdateRequest(
        @NotNull(message = "변경할 배달 상태는 필수입니다.")
        DeliveryStatus status
) {
}