package org.example.eatopia.domain.cart.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.consts.Const;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.example.eatopia.domain.product.entity.Product;
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

        List<CartItem> cartItems = cartItemRepository.findAllByCartWithProduct(cart.getId());

        List<CartItemResponse> itemResponses = cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    return CartItemResponse.of(cartItem, product.getName(), product.getPrice());
                })
                .collect(Collectors.toList());

        // 총액,배송비,최종 금액 계산
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal deliveryFee = Const.DEFAULT_DELIVERY_PRICE;
        BigDecimal finalAmount = totalAmount.add(Const.DEFAULT_DELIVERY_PRICE);

        return CartResponse.of(cart, itemResponses, totalAmount, deliveryFee, finalAmount);
    }

    @Override
    public Cart getCart(Long userId) {

        return cartRepository.findByUserIdOrThrow(userId);
    }

    @Override
    public List<CartItem> getSelectedCartItems(Long userId) {

        return cartItemRepository.findSelectedItemsForOrder(userId);
    }
}
