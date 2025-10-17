package org.example.eatopia.domain.cart.service.query;

import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.entity.Cart;

public interface CartQueryService {

    CartResponse getCartByUser(Long userId);

    Cart getCart(Long userId);
}
