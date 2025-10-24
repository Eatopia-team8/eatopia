package org.example.eatopia.domain.cart.service.query;

import lombok.RequiredArgsConstructor;
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

    /**
     * 사용자가 주문하기 위해 선택한 장바구니 상품 목록을 조회합니다.
     * <p>
     * 내부적으로 {@link CartItemRepository#findSelectedItemsForOrder(Long)} 메서드를 호출하여,
     * userId의 장바구니 항목 중 {@code isSelected = true}인 상품만 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 선택된 장바구니 상품 목록 ({@link CartItem} 리스트)
     */
    @Override
    public List<CartItem> getSelectedCartItems(Long userId) {

        return cartItemRepository.findSelectedItemsForOrder(userId);
    }
}
