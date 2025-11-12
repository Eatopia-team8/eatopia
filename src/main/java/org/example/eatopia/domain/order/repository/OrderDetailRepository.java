package org.example.eatopia.domain.order.repository;

import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);

    @Query("SELECT od FROM OrderDetail od JOIN od.order o WHERE od.id = :orderDetailId AND o.user.id = :userId")
    Optional<OrderDetail> findByIdAndOrderUserId(Long orderDetailId, Long userId);

    @Query("SELECT od FROM OrderDetail od JOIN FETCH od.order o WHERE od.sellerId = :sellerId AND od.settlement IS NULL AND o.status = :status")
    List<OrderDetail> findUnsettledOrderDetailsBySellerId(Long sellerId, OrderStatus status);
}
