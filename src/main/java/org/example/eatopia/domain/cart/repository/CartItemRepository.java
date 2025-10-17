package org.example.eatopia.domain.cart.repository;

import org.example.eatopia.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
