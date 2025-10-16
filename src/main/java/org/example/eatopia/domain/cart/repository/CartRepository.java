package org.example.eatopia.domain.cart.repository;

import org.example.eatopia.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
