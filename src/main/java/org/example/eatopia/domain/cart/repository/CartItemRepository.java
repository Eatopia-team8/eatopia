package org.example.eatopia.domain.cart.repository;

import org.example.eatopia.domain.cart.entity.Cart;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCart(Cart cart);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE ci.product.id = :productId AND c.userId = :userId")
    Optional<CartItem> findItemForUser(@Param("productId") Long productId,
                                       @Param("userId") Long userId);
}
