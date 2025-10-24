package org.example.eatopia.domain.product.service.query;

import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.dto.response.ProductListResponse;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.springframework.data.domain.Pageable;

public interface ProductQueryService {

    ProductResponse getProduct(Long productId);

    ProductListResponse searchProducts(ProductSearchCondition condition, Pageable pageable);

    ProductResponse getProductWithCache(Long productId);

    ProductListResponse searchProductsWithCache(ProductSearchCondition condition, Pageable pageable);
    
    // 상품 ID로 조회 없으면 예외
    Product getProductOrElseThrow(Long productId);
}