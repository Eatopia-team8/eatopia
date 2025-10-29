package org.example.eatopia.domain.productImage.validator;

import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.productImage.dto.request.ProductImageInfo;
import org.example.eatopia.domain.productImage.exception.ProductImageErrorCode;

import java.util.List;

public class ProductImageValidator {

    private static final int MAX_IMAGE_COUNT = 10;

    public void validateImages(List<ProductImageInfo> images) {
        if (images == null || images.isEmpty()) {
            throw new ProductException(ProductImageErrorCode.PRD_IMAGE_REQUIRED);
        }

        if (images.size() > MAX_IMAGE_COUNT) {
            throw new ProductException(ProductImageErrorCode.PRD_IMAGE_EXCEED_LIMIT);
        }

        // 대표 이미지가 정확히 1개인지 검증
        long thumbnailCount = images.stream()
                .filter(ProductImageInfo::isThumbnail)
                .count();

        if (thumbnailCount != 1) {
            throw new ProductException(ProductImageErrorCode.PRD_INVALID_THUMBNAIL_COUNT);
        }

        // displayOrder 중복 검증
        long distinctOrderCount = images.stream()
                .map(ProductImageInfo::displayOrder)
                .distinct()
                .count();

        if (distinctOrderCount != images.size()) {
            throw new ProductException(ProductImageErrorCode.PRD_DUPLICATE_IMAGE_ORDER);
        }
    }
}
