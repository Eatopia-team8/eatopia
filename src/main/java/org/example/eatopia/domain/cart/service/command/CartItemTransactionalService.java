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

@Service
@RequiredArgsConstructor
public class CartItemTransactionalService {

    private final CartItemRepository cartItemRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseCartItemQuantity(Cart cart, Product product, int quantity) {
        CartItem cartItem;

        try {
            // CartItem 잠금 조회 (비관적 락)
            cartItem = cartItemRepository.findLockedByCartIdAndProductId(cart.getId(), product.getId())
                    .orElseGet(() -> cartItemRepository.save(CartItem.create(cart, product, 0)));
        } catch (DataIntegrityViolationException e) {
            // 다른 트랜잭션이 동시에 생성했을 경우 재조회
            cartItem = cartItemRepository.findLockedByCartIdAndProductId(cart.getId(), product.getId())
                    .orElseThrow(() -> new GlobalException(CartErrorCode.CONCURRENT_MODIFICATION));
        }

        cartItem.addQuantity(quantity);
    }
}
