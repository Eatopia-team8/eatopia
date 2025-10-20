package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.dto.request.CartCreateRequest;
import org.example.eatopia.domain.cart.dto.request.CartUpdateQuantityRequest;
import org.example.eatopia.domain.cart.dto.response.CartCreateResponse;
import org.example.eatopia.domain.cart.dto.response.CartItemResponse;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.exception.CartErrorCode;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.cart.repository.CartRepository;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartCommandServiceImpl implements CartCommandService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductQueryService productQueryService;

    @Override
    public CartCreateResponse createCartItem(Long userId, CartCreateRequest request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.create(userId)));

        // TODO: 상품의 재고가 없으면 예외 반환

        // CartItem 생성
        // TODO: 상품 엔티티 가격, 이름 get
        CartItem cartItem = CartItem.create(cart, request.productId(), request.quantity());
        cartItemRepository.save(cartItem);

        return CartCreateResponse.from("상품이름");
    }

    @Override
    public CartItemResponse updateQuantity(Long productId, CartUpdateQuantityRequest request, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);

        CartItem cartItem = cartItemRepository.findItemForUser(productId, userId)
                .orElseThrow(() -> new GlobalException(CartErrorCode.USER_CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(request.operation());

        return CartItemResponse.of(cartItem, product.getName(), product.getPrice());
    }
}
