package org.example.eatopia.domain.cart.service.command;

import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;

public interface CartCommandService {
    CartCreateResponse createCartItem(Long userId, CartCreateRequest request);
}
