package org.example.eatopia.domain.product.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.request.ProductUpdateRequest;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    // 생성용
    public void validateCreateRequest(ProductCreateRequest request) {
        validatePrice(request.price());
        validateStock(request.stock());
    }

    // 수정용
    public void validateUpdateRequest(ProductUpdateRequest request) {
        if (request.price() != null) validatePrice(request.price());
        if (request.stock() != null) validateStock(request.stock());
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_PRICE);
        }
    }

    private void validateStock(Long stock) {
        if (stock == null || stock < 0) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_STOCK);
        }
    }

    // 카테고리 검증
    public void validateCategory(Category category) {
        if (category.getParent() == null) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_CATEGORY);
        }
    }
}
