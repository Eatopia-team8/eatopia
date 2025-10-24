package org.example.eatopia.domain.cart.service.query;

import org.example.eatopia.domain.cart.dto.response.CartResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.repository.CartItemRepository;

import java.util.List;

public interface CartQueryService {

    CartResponse getCartByUser(Long userId);

    Cart getCart(Long userId);

    /**
     * 사용자가 주문하기 위해 선택한 장바구니 상품 목록을 조회합니다.
     * <p>
     * 내부적으로 {@link CartItemRepository#findSelectedItemsForOrder(Long)} 메서드를 호출하여,
     * userId의 장바구니 항목 중 {@code isSelected = true}인 상품만 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 선택된 장바구니 상품 목록 ({@link CartItem} 리스트)
     */
    List<CartItem> getSelectedCartItems(Long userId);
}
