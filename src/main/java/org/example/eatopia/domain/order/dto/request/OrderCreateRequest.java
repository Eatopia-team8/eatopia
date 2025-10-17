package org.example.eatopia.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotNull(message = "상품 ID가 필요합니다.")
        Long productId,
        @NotNull(message = "판매자 ID가 필요합니다.")
        Long sellerId
) {

}
