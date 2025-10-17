package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandServiceImpl implements CartCommandService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartCreateResponse createCartItem(Long userId, CartCreateRequest request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));

        // TODO: 상품의 재고가 없으면 예외 반환

        // CartItem 생성
        // TODO: 상품 엔티티 가격, 이름 get
        CartItem cartItem = CartItem.create(cart, request.productId(), request.quantity(), BigDecimal.ONE);
        cartItemRepository.save(cartItem);

        return CartCreateResponse.from("상품이름");
    }
}
