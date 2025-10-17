package org.example.eatopia.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.product.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        String description,
        String thumbnailUrl,

        @NotNull(message = "가격은 필수입니다.")
        BigDecimal price,

        @NotNull(message = "재고는 필수입니다.")
        Long stock,

        @NotNull(message = "상태는 필수입니다.")
        ProductStatus status,

        Long categoryId
) {
}
