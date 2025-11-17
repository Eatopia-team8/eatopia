package org.example.eatopia.domain.productImage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.productImage.dto.request.ProductImageAddRequest;
import org.example.eatopia.domain.productImage.dto.request.ProductImageOrderUpdateRequest;
import org.example.eatopia.domain.productImage.dto.response.ProductImageResponse;
import org.example.eatopia.domain.productImage.service.command.ProductImageService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    // 이미지 개별 추가 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PostMapping("/v1/products/{productId}/images")
    public ResponseEntity<Response<ProductImageResponse>> addProductImage(@PathVariable Long productId,
                                                                          @Valid @RequestBody ProductImageAddRequest request,
                                                                          @AuthenticationPrincipal UserPrincipal authUser) {

        ProductImageResponse response = productImageService.addProductImage(
                productId, request, authUser.getId()
        );

        return ResponseEntity.ok(Response.success(response));
    }

    // 이미지 순서 변경 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PatchMapping("/v1/products/{productId}/images/{imageId}/order")
    public ResponseEntity<Response<Void>> updateImageOrder(@PathVariable Long productId,
                                                           @PathVariable Long imageId,
                                                           @Valid @RequestBody ProductImageOrderUpdateRequest request,
                                                           @AuthenticationPrincipal UserPrincipal authUser) {

        productImageService.updateImageOrder(
                productId, imageId, request.displayOrder(), authUser.getId()
        );

        return ResponseEntity.ok(Response.success());
    }

    // 대표 이미지 변경 (판매자)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @PatchMapping("/v1/products/{productId}/images/{imageId}/thumbnail")
    public ResponseEntity<Response<Void>> updateThumbnail(@PathVariable Long productId,
                                                          @PathVariable Long imageId,
                                                          @AuthenticationPrincipal UserPrincipal authUser) {

        productImageService.updateThumbnail(productId, imageId, authUser.getId());

        return ResponseEntity.ok(Response.success());
    }

    // 이미지 개별 삭제 (판매자, 어드민)
    @PreAuthorize("hasAnyRole('ROLE_SELLER', 'ROLE_ADMIN')")
    @DeleteMapping("/v1/products/{productId}/images/{imageId}")
    public ResponseEntity<Response<Void>> deleteProductImage(@PathVariable Long productId,
                                                             @PathVariable Long imageId,
                                                             @AuthenticationPrincipal UserPrincipal authUser) {

        productImageService.deleteProductImage(
                productId, imageId, authUser.getId(), authUser.getUserRole()
        );

        return ResponseEntity.ok(Response.success());
    }
}
