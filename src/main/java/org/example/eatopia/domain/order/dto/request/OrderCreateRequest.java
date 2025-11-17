package org.example.eatopia.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotNull(message = "배송지 ID는 필수입니다.")
        Long addressId,
        Long couponIssueId
) {
}
