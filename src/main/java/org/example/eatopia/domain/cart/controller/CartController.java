package org.example.eatopia.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsDeleteRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsSelectionRequest;
import org.example.eatopia.domain.cart.dto.request.CartUpdateQuantityRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.service.command.CartCommandService;
import org.example.eatopia.domain.cart.service.query.CartQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;

    @PostMapping("/v1/carts/items")
    public ResponseEntity<Response<CartCreateResponse>> createCartItem(@Valid @RequestBody CartCreateRequest request,
                                                                       @AuthenticationPrincipal UserPrincipal authUser) {

        CartCreateResponse createCartItem = cartCommandService.createCartItem(authUser.getId(), request);

        return ResponseEntity.ok(Response.success(createCartItem));
    }

    @GetMapping("/v1/carts")
    public ResponseEntity<Response<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal authUser) {

        CartResponse cartResponse = cartQueryService.getCartByUser(authUser.getId());

        return ResponseEntity.ok(Response.success(cartResponse));
    }

    @PatchMapping("/v1/carts/items/{productId}")
    public ResponseEntity<Response<CartItemResponse>> updateQuantity(@PathVariable Long productId,
                                                                     @Valid @RequestBody CartUpdateQuantityRequest request,
                                                                     @AuthenticationPrincipal UserPrincipal authUser) {

        CartItemResponse cartItemResponse = cartCommandService.updateQuantity(productId, request, authUser.getId());

        return ResponseEntity.ok(Response.success(cartItemResponse));
    }


    @PatchMapping("/v1/carts/items/select")
    public ResponseEntity<Response<Void>> updateItemSelections(@Valid @RequestBody CartItemsSelectionRequest request,
                                                               @AuthenticationPrincipal UserPrincipal authUser) {

        cartCommandService.updateItemSelections(request, authUser.getId());

        return ResponseEntity.ok(Response.success());
    }

    @DeleteMapping("/v1/carts/items")
    public ResponseEntity<Response<Void>> deleteItem(@Valid @RequestBody CartItemsDeleteRequest request,
                                                     @AuthenticationPrincipal UserPrincipal authUser) {

        cartCommandService.deleteItems(request, authUser.getId());

        return ResponseEntity.ok(Response.success());
    }
}
