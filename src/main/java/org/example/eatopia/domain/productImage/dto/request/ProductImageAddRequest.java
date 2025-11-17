package org.example.eatopia.domain.productImage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductImageAddRequest(
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @NotNull(message = "이미지 순서는 필수입니다.")
        Integer displayOrder,

        Boolean isThumbnail
) {
    public ProductImageAddRequest {
        if (isThumbnail == null) {
            isThumbnail = false;
        }
    }
}