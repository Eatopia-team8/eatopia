package org.example.eatopia.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.request.ProductUpdateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;

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

    // 상품 삭제 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @DeleteMapping("/v1/products/{productId}")
    public ResponseEntity<Response<Void>> deleteProduct(@PathVariable Long productId,
                                                        @AuthenticationPrincipal UserPrincipal authUser) {

        productCommandService.deleteProduct(productId, authUser.getId());
        
        return ResponseEntity.ok(Response.success());
    }
}
