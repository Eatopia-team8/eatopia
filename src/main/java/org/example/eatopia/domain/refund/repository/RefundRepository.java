package org.example.eatopia.domain.refund.repository;

import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    boolean existsByOrderDetailId(Long orderDetailId);

    Page<Refund> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT SUM(r.quantity) FROM Refund r " +
            "WHERE r.orderDetail.id = :orderDetailId AND r.status = 'SUCCESS'")
    Optional<Integer> sumSuccessQuantityByOrderDetailId(Long orderDetailId);

    //성공 상태인 환불 금액 조회
    @Query("SELECT SUM(r.amount) FROM Refund r " +
            "WHERE r.payment.id = :paymentId AND r.status = 'SUCCESS'")
    Optional<BigDecimal> sumSuccessAmountByPaymentId(Long paymentId);

    //SUCCESS, PENDING' 상태인 환불이 존재하는지 확인
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Refund r " +
            "WHERE r.orderDetail.id = :orderDetailId AND r.status IN ('SUCCESS', 'PENDING')")
    boolean existsActiveRefundByOrderDetailId(Long orderDetailId);

    @Query("SELECT r FROM Refund r JOIN r.orderDetail od WHERE od.sellerId = :sellerId AND r.settlement IS NULL AND r.status = :status")
    List<Refund> findUnsettleRefundsBySellerId(Long sellerId, RefundStatus status);
}