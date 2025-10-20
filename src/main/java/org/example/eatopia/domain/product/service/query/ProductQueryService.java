package org.example.eatopia.domain.product.service.query;

import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryService {

    ProductResponse getProduct(Long productId);

    // 상품 ID로 조회 없으면 예외
    Product getProductOrElseThrow(Long productId);

    Page<ProductResponse> searchProducts(ProductSearchCondition condition, Pageable pageable);
}