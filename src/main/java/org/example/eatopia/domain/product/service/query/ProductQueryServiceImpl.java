package org.example.eatopia.domain.product.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.dto.response.ProductListResponse;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    // 상품 단건 조회
    @Override
    @Cacheable(value = "product", key = "#productId", unless = "#result == null")
    public ProductResponse getProduct(Long productId) {
        Product product = getProductOrElseThrow(productId);
        return ProductResponse.from(product);
    }

    @Override
    public Product getProductOrElseThrow(Long productId) {
        return productRepository.findWithCategoryAndSellerById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRD_ID_NOT_FOUND));
    }

    @Override
    @Cacheable(
            value = "productList",
            key = "#condition.hashCode() + '_' + #pageable.pageNumber + '_' + #pageable.pageSize",
            unless = "#result == null || #result.content().isEmpty()"
    )
    public ProductListResponse searchProducts(ProductSearchCondition condition, Pageable pageable) {

        Page<Product> products = productRepository.searchProducts(condition, pageable);

        Page<ProductResponse> productResponsePage = products.map(ProductResponse::from);

        return ProductListResponse.from(productResponsePage);
    }
}
