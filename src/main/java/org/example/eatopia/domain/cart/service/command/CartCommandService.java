package org.example.eatopia.domain.cart.service.command;

import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemSelectionRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsSelectionRequest;
import org.example.eatopia.domain.cart.dto.request.CartUpdateQuantityRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;

public interface CartCommandService {
    CartCreateResponse createCartItem(Long userId, CartCreateRequest request);

    CartItemResponse updateQuantity(Long productId, CartUpdateQuantityRequest request, Long userId);

    void updateItemSelection(Long productId, CartItemSelectionRequest request, Long userId);

    void updateItemSelections(CartItemsSelectionRequest request, Long userId);

    void deleteItem(Long productId, Long userId);
}
