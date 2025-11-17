package org.example.eatopia.domain.productImage.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductImageInfo(

        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @NotNull(message = "이미지 순서는 필수입니다.")
        Integer displayOrder,

        @JsonProperty("isThumbnail") @NotNull(message = "대표 이미지 여부는 필수입니다.")
        Boolean isThumbnail
) {
}
