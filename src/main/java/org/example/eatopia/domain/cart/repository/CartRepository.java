package org.example.eatopia.domain.cart.repository;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.exception.CartErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    default Cart findByUserIdOrThrow(Long userId) {
        return findByUserId(userId)
                .orElseThrow(() -> new GlobalException(CartErrorCode.CART_NOT_FOUND));
    }
}
