package org.example.eatopia.domain.productImage.validator;

import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.exception.ProductImageErrorCode;
import org.example.eatopia.domain.productImage.exception.ProductImageException;
import org.springframework.stereotype.Component;

@Component
public class ProductImageValidator {

    private static final int MAX_IMAGE_COUNT = 10;

    // 이미지 추가 시 최대 개수 검증
    public void validateImageCount(long currentCount) {
        if (currentCount >= MAX_IMAGE_COUNT) {
            throw new ProductImageException(ProductImageErrorCode.PRD_IMAGE_EXCEED_LIMIT);
        }
    }

    // 이미지 순서 변경시 검증
    public void validateImageOrderUpdate(ProductImage target, Long productId, Integer newOrder, int totalImageCount) {
        // 타겟이 해당 상품의 이미지인지 검증
        if (!target.getProduct().getId().equals(productId)) {
            throw new ProductImageException(ProductImageErrorCode.PRD_IMAGE_NOT_BELONG_TO_PRODUCT);
        }

        // newOrder 범위 검증
        if (newOrder < 0 || newOrder >= totalImageCount) {
            throw new ProductImageException(ProductImageErrorCode.PRD_IMAGE_INVALID_DISPLAY_ORDER);
        }
    }
}
