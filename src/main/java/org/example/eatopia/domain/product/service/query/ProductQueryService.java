package org.example.eatopia.domain.product.service.query;

import org.example.eatopia.domain.product.entity.Product;

public interface ProductQueryService {

    // 상품 ID로 조회 없으면 예외
    Product getProductOrElseThrow(Long productId);
}