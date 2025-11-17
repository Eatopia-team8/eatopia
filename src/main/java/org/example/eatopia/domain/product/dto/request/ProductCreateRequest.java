package org.example.eatopia.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.productImage.dto.request.ProductImageInfo;

import java.math.BigDecimal;
import java.util.List;

public record ProductCreateRequest(

        @NotBlank(message = "상품명은 필수입니다.")
        String name,

        String description,

        @NotNull(message = "가격은 필수입니다.")
        BigDecimal price,

        @NotNull(message = "재고는 필수입니다.")
        Integer stock,

        @NotNull(message = "상태는 필수입니다.")
        ProductStatus status,

        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @NotEmpty(message = "이미지는 최소 1개 이상 필요합니다.")
        List<ProductImageInfo> images
) {
}
