package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsDeleteRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemsSelectionRequest;
import org.example.eatopia.domain.cart.dto.request.CartUpdateQuantityRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.exception.CartErrorCode;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartCommandServiceImpl implements CartCommandService {

    private static final int MAX_RETRY = 3; // 재시도 횟수
    private static final long RETRY_DELAY_MS = 50L;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductQueryService productQueryService;
    private final UserQueryService userQueryService;
    private final CartItemTransactionalService cartItemTransactionalService;

    @Override
    public CartCreateResponse createCartItem(Long userId, CartCreateRequest request) {

        Product product = productQueryService.getProductOrElseThrow(request.productId());

        // 판매상품인지 체크
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new GlobalException(CartErrorCode.PRODUCT_NOT_FOR_SALE);
        }

        User user = userQueryService.getUserEntityById(userId);
        // Cart 조회 + 없으면 생성
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(user)));

        int retryCount = 0;
        while (true) {  // 재시도 루프
            try {
                // CartItem 수량 증가 /생성
                cartItemTransactionalService.increaseCartItemQuantity(cart, product, request.quantity());
                break;
            } catch (CannotAcquireLockException | DataIntegrityViolationException e) {
                retryCount++;
                if (retryCount >= MAX_RETRY) {
                    throw new GlobalException(CartErrorCode.CONCURRENT_MODIFICATION);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);   // 대기 후 재시도
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return CartCreateResponse.from(product.getName());
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(Long productId, CartUpdateQuantityRequest request, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);

        CartItem cartItem = cartItemRepository.findItemForUser(productId, userId)
                .orElseThrow(() -> new GlobalException(CartErrorCode.USER_CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(request.operation());

        return CartItemResponse.of(cartItem, product.getName(), product.getPrice());
    }

    @Override
    @Transactional
    public void updateItemSelections(CartItemsSelectionRequest request, Long userId) {

        cartItemRepository.updateSelectionItems(userId, request.productIds(), request.isSelected());
    }

    @Override
    @Transactional
    public void deleteItems(CartItemsDeleteRequest request, Long userId) {

        cartItemRepository.deleteSelectedItems(userId, request.productIds());
    }

    @Override
    @Transactional
    public void deleteOrderedItems(Long userId, List<Long> orderedProductIds) {

        cartItemRepository.deleteSelectedItems(userId, orderedProductIds);
    }
}
