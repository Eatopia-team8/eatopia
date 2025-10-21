package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartItemSelectionRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandServiceImpl implements CartCommandService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductQueryService productQueryService;

    @Override
    public CartCreateResponse createCartItem(Long userId, CartCreateRequest request) {

        Product product = productQueryService.getProductOrElseThrow(request.productId());

        // 판매상품인지 체크
        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new GlobalException(CartErrorCode.PRODUCT_NOT_FOR_SALE);
        }

        // Cart 조회 + 없으면 생성
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));

        // CartItem 조회 후 존재하면 수량 증가, 없으면 새로 생성
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (cartItem != null) {
            cartItem.addQuantity(request.quantity());
        } else {
            cartItem = CartItem.create(cart, product, request.quantity());
            cartItemRepository.save(cartItem);
        }

        return CartCreateResponse.from(product.getName());
    }

    @Override
    public CartItemResponse updateQuantity(Long productId, CartUpdateQuantityRequest request, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);

        CartItem cartItem = cartItemRepository.findItemForUser(productId, userId)
                .orElseThrow(() -> new GlobalException(CartErrorCode.USER_CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(request.operation());

        return CartItemResponse.of(cartItem, product.getName(), product.getPrice());
    }

    @Override
    public void updateItemSelection(Long productId, CartItemSelectionRequest request, Long userId) {

        CartItem cartItem = cartItemRepository.findItemForUser(productId, userId)
                .orElseThrow(() -> new GlobalException(CartErrorCode.USER_CART_ITEM_NOT_FOUND));

        cartItem.updateIsSelected(request.isSelected());
    }

    @Override
    public void updateItemSelections(CartItemsSelectionRequest request, Long userId) {

        if (request.productIds().isEmpty()) return;

        List<CartItem> cartItems = cartItemRepository.findAllByUserIdAndProductIdIn(userId, request.productIds());

        cartItems.forEach(cartItem -> cartItem.updateIsSelected(request.isSelected()));
    }
}
