package org.example.eatopia.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    // 상품 등록 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/v1/products")
    public ResponseEntity<Response<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {

        ProductResponse response = productCommandService.createProduct(request);

        return ResponseEntity.ok(Response.success(response));
    }
}
