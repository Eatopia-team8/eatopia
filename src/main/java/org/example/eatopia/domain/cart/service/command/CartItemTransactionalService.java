package org.example.eatopia.domain.cart.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.exception.CartErrorCode;
import org.example.eatopia.domain.cart.repository.CartItemRepository;
import org.example.eatopia.domain.product.entity.Product;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemTransactionalService {

    private final CartItemRepository cartItemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseCartItemQuantity(Cart cart, Product product, int quantity) {

        // CartItem 잠금 조회 (비관적 락)
        Optional<CartItem> cartItemOptional = cartItemRepository.findLockedByCartIdAndProductId(cart.getId(), product.getId());

        if (cartItemOptional.isPresent()) {
            // 상품이 있으면 수량 증가
            CartItem cartItem = cartItemOptional.get();
            cartItem.addQuantity(quantity);
        } else {
            // 상품이 없으면 새로 생성
            try {
                CartItem cartItem = CartItem.create(cart, product, quantity);
                cartItemRepository.save(cartItem);
            } catch (DataIntegrityViolationException e) {
                // 다른 트랜잭션이 동시에 생성했을 경우 재조회 후, 수량 증가
                CartItem cartItem = cartItemRepository.findLockedByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseThrow(() -> new GlobalException(CartErrorCode.CONCURRENT_MODIFICATION));
                cartItem.addQuantity(quantity);
            }
        }
    }
}
