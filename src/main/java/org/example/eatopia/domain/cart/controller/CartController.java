package org.example.eatopia.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.auth.dto.AuthUser;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.service.command.CartCommandService;
import org.example.eatopia.domain.cart.service.query.CartQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @PostMapping("/v1/carts/items")
    public ResponseEntity<Response<CartCreateResponse>> createCartItem(@RequestBody CartCreateRequest request,
                                                                       @AuthenticationPrincipal AuthUser authUser) {

        CartCreateResponse createCartItem = cartCommandService.createCartItem(authUser.getId(), request);
        return ResponseEntity.ok(Response.success(createCartItem));
    }
}
