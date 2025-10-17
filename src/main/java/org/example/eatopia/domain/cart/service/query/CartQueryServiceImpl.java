package org.example.eatopia.domain.cart.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryServiceImpl implements CartQueryService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponse getCartByUser(Long userId) {

        Cart cart = getCart(userId);

        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

        // TODO: 추후 상품명 컬럼 추가
        // 상품 이름은 임시로 "상품명"으로 처리
        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(cartItem -> CartItemResponse.of(cartItem, "상품명"))
                .collect(Collectors.toList());

        // 총액,할인,최종 금액 계산
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountAmount = BigDecimal.ZERO; // TODO: 할인 계산 로직
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        return CartResponse.of(cart, itemResponses, totalAmount, discountAmount, finalAmount);
    }

    @Override
    public Cart getCart(Long userId) {
        return cartRepository.findByUserIdOrThrow(userId);
    }
}
