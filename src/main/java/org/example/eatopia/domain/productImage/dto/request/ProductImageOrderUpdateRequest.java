package org.example.eatopia.domain.productImage.dto.request;

import jakarta.validation.constraints.NotNull;

// 이미지 순서 변경 요청
public record ProductImageOrderUpdateRequest(

        @NotNull(message = "새로운 순서는 필수입니다.")
        Integer displayOrder
) {
}
