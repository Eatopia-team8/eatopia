package org.example.eatopia.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.service.command.CartCommandService;
import org.example.eatopia.domain.cart.service.query.CartQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @PostMapping("/v1/carts/items")
    public ResponseEntity<Response<CartCreateResponse>> createCartItem(@RequestBody CartCreateRequest request) {

        // 인증 전 userId 하드코딩
        Long userId = 1L;
        CartCreateResponse createCartItem = cartCommandService.createCartItem(userId, request);
        return ResponseEntity.ok(Response.success(createCartItem));
    }
}
