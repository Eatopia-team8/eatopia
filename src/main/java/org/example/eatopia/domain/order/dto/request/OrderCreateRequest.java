package org.example.eatopia.domain.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
        @NotNull(message = "상품 ID가 필요합니다.")
        Long productId,
        @NotNull(message = "판매자 ID가 필요합니다.")
        Long sellerId,
        @NotNull(message = "수량이 필요합니다.")
        @Min(value = 1, message = "수량은 1개 이상이여야 합니다.")
        Long quantity
) {

}
