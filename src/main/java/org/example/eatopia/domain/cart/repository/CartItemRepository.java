package org.example.eatopia.domain.cart.repository;

import org.example.eatopia.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product p WHERE ci.cart.id = :cartId")
    List<CartItem> findAllByCartWithProduct(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c WHERE ci.product.id = :productId AND c.user.id = :userId")
    Optional<CartItem> findItemForUser(@Param("productId") Long productId,
                                       @Param("userId") Long userId);

    CartItem findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("UPDATE CartItem ci " +
            "SET ci.isSelected = :isSelected " +
            "WHERE ci.cart.user.id = :userId " +
            "AND ci.product.id IN :productIds")
    void updateSelectionItems(@Param("userId") Long userId,
                              @Param("productIds") List<Long> productIds,
                              @Param("isSelected") Boolean isSelected);

    @Modifying
    @Query("DELETE FROM CartItem ci " +
            "WHERE ci.cart.user.id = :userId " +
            "AND ci.product.id IN :productIds " +
            "AND ci.isSelected = true")
    void deleteSelectedItems(@Param("userId") Long userId,
                             @Param("productIds") List<Long> productIds);

    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.product p " +
            "JOIN ci.cart c " +
            "WHERE c.user.id = :userId AND ci.isSelected = true")
    List<CartItem> findSelectedItemsForOrder(@Param("userId") Long userId);
}
