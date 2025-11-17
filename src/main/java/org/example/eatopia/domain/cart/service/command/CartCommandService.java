package org.example.eatopia.domain.cart.service.command;

import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsDeleteRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsSelectionRequest;
import org.example.eatopia.domain.cart.dto.request.CartUpdateQuantityRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.repository.CartItemRepository;

import java.util.List;

public interface CartCommandService {
    CartCreateResponse createCartItem(Long userId, CartCreateRequest request);

    CartItemResponse updateQuantity(Long productId, CartUpdateQuantityRequest request, Long userId);

    void updateItemSelections(CartItemsSelectionRequest request, Long userId);

    void deleteItems(CartItemsDeleteRequest request, Long userId);

    /**
     * 사용자가 주문을 완료한 후, 해당 상품들을 장바구니에서 삭제합니다.
     * <p>
     * 내부적으로 {@link CartItemRepository#deleteSelectedItems(Long, List)} 메서드를 호출하여
     * 지정된 사용자 ID와 상품 ID 목록에 해당하는 장바구니 항목을 삭제합니다.
     * 주문 완료 시 장바구니 정리 용도로 사용됩니다.
     *
     * @param userId            장바구니를 소유한 사용자 ID
     * @param orderedProductIds 삭제할 상품의 ID 목록
     */
    void deleteOrderedItems(Long userId, List<Long> orderedProductIds);
}
