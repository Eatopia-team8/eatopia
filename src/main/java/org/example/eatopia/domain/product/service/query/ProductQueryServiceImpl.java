package org.example.eatopia.domain.product.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.dto.response.ProductListResponse;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.repository.ProductRepository;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.repository.ProductImageRepository;
import org.example.eatopia.domain.search.service.query.SearchKeywordQueryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final SearchKeywordQueryService searchKeywordQueryService;

    // ----- V1: 캐시 없음 -----
    @Override
    public ProductResponse getProduct(Long productId) {

        Product product = getProductOrElseThrow(productId);

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        return ProductResponse.of(product, images);
    }

    @Override
    public ProductListResponse searchProducts(ProductSearchCondition condition, Pageable pageable) {

        Page<Product> productPage = productRepository.searchProducts(condition, pageable);

        return buildProductListResponse(productPage);
    }

    // ----- V2: 캐시 있음 (전체 캐시) -----
    @Override
    @Cacheable(value = "product", key = "#productId", unless = "#result == null")
    public ProductResponse getProductWithCache(Long productId) {

        return getProduct(productId);
    }

    @Override
    @Cacheable(
            value = "productList",
            key = "T(String).valueOf(#condition) + '_' + #pageable.pageNumber + '_' + #pageable.pageSize",
            unless = "#result == null || #result.content().isEmpty()"
    )
    public ProductListResponse searchProductsWithCache(ProductSearchCondition condition, Pageable pageable) {

        return searchProducts(condition, pageable);
    }

    // ----- V3: 선택적 캐시 (인기 검색어는 컨트롤러에서 처리) -----
    @Override
    @Cacheable(
            value = "productListV3",
            key = "#condition.categoryId() + '_' + #pageable.pageNumber + '_' + #pageable.pageSize",
            condition = "@cacheConditionHelper.isCacheableForProductListV3(#condition, #pageable)",
            unless = "#result == null || #result.content().isEmpty()"
    )
    public ProductListResponse searchProductsV3(ProductSearchCondition condition, Pageable pageable) {

        return searchProducts(condition, pageable);
    }

    @Override
    public Product getProductOrElseThrow(Long productId) {

        return productRepository.findWithCategoryAndSellerById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRD_ID_NOT_FOUND));
    }

    // ProductListResponse 생성 (공통 로직)
    private ProductListResponse buildProductListResponse(Page<Product> productPage) {
        // 상품 ID 목록 추출
        List<Long> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .toList();

        // 이미지 일괄 조회 (N+1 방지)
        Map<Long, List<ProductImage>> imageMap = productImageRepository
                .findAllByProductIdOrderByDisplayOrderAsc(productIds)
                .stream()
                .collect(Collectors.groupingBy(img -> img.getProduct().getId()));

        // ProductResponse 생성
        List<ProductResponse> products = productPage.getContent().stream()
                .map(product -> ProductResponse.of(
                        product,
                        imageMap.getOrDefault(product.getId(), Collections.emptyList())
                ))
                .toList();

        return ProductListResponse.of(products, productPage);
    }
}

