package org.example.eatopia.domain.order.repository;

import org.example.eatopia.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product p " +
            "LEFT JOIN FETCH o.couponIssue ci " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE o.user.id = :userId AND o.id = :orderId")
    Optional<Order> findByUserIdAndId(Long userId, Long orderId);

    boolean existsByUserId(Long userId);

    Optional<Order> findByCode(String code);
}
