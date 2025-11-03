package org.example.eatopia.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.annotation.LogExecutionTime;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.dto.request.ProductUpdateRequest;
import org.example.eatopia.domain.product.dto.response.ProductListResponse;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.search.service.query.SearchKeywordQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final SearchKeywordQueryService searchKeywordQueryService;

    // 상품 등록 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/v1/products")
    public ResponseEntity<Response<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal authUser) {

        ProductResponse response = productCommandService.createProduct(request, authUser.getId());

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 수정 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PatchMapping("/v1/products/{productId}")
    public ResponseEntity<Response<ProductResponse>> updateProduct(@PathVariable Long productId,
                                                                   @RequestBody ProductUpdateRequest request,
                                                                   @AuthenticationPrincipal UserPrincipal authUser) {

        ProductResponse response = productCommandService.updateProduct(productId, request, authUser.getId());

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 삭제 (판매자, 어드민)
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    @DeleteMapping("/v1/products/{productId}")
    public ResponseEntity<Response<Void>> deleteProduct(@PathVariable Long productId,
                                                        @AuthenticationPrincipal UserPrincipal authUser) {

        productCommandService.deleteProduct(productId, authUser.getId(), authUser.getUserRole());

        return ResponseEntity.ok(Response.success());
    }

    // 상품 단건 조회 (공통)
    @LogExecutionTime("V1 - No Cache - 상품 단건 조회")
    @GetMapping("/v1/products/{productId}")
    public ResponseEntity<Response<ProductResponse>> getProduct(@PathVariable Long productId) {

        ProductResponse response = productQueryService.getProduct(productId);

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 목록 조회 (공통)
    @LogExecutionTime("V1 - No Cache - 상품 목록 조회")
    @GetMapping("/v1/products")
    public ResponseEntity<Response<ProductListResponse>> searchProducts(@ModelAttribute ProductSearchCondition condition,
                                                                        @PageableDefault(size = 10) Pageable pageable) {

        ProductListResponse response = productQueryService.searchProducts(condition, pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 단건 조회 (공통) - with Cache
    @LogExecutionTime("V2 - With Cache - 상품 단건 조회")
    @GetMapping("/v2/products/{productId}")
    public ResponseEntity<Response<ProductResponse>> getProductWithCache(@PathVariable Long productId) {

        ProductResponse response = productQueryService.getProductWithCache(productId);

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 목록 조회 (공통) - with Cache
    @LogExecutionTime("V2 - With Cache - 상품 목록 조회")
    @GetMapping("/v2/products")
    public ResponseEntity<Response<ProductListResponse>> searchProductsWithCache(@ModelAttribute ProductSearchCondition condition,
                                                                                 @PageableDefault(size = 10) Pageable pageable) {

        ProductListResponse response = productQueryService.searchProductsWithCache(condition, pageable);

        return ResponseEntity.ok(Response.success(response));
    }

    // 상품 목록 조회
    // - 모든 검색 조건 지원 (keyword, status, price, sortBy 등)
    // - categoryId + 최신순 + 페이지<10 + 필터 없을 때만 캐시
    // - 키워드 검색 시 인기 검색어 집계
    @LogExecutionTime("V3 - Selective Cache & Keyword Tracking - 상품 목록 조회")
    @GetMapping("/v3/products")
    public ResponseEntity<Response<ProductListResponse>> searchProductsV3(@ModelAttribute ProductSearchCondition condition,
                                                                          @PageableDefault(size = 10) Pageable pageable) {

        // 인기 검색어 집계 (캐시 여부와 무관하게 항상 실행)
        searchKeywordQueryService.recordKeyword(condition.keyword());

        ProductListResponse response = productQueryService.searchProductsV3(condition, pageable);

        return ResponseEntity.ok(Response.success(response));
    }
}
