package org.example.eatopia.domain.cart.service.query;

import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;

import java.util.List;

public interface CartQueryService {

    CartResponse getCartByUser(Long userId);

    Cart getCart(Long userId);

    List<CartItem> getSelectedCartItems(Long userId);
}
