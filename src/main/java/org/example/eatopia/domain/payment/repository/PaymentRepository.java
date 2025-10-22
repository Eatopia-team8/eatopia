package org.example.eatopia.domain.payment.repository;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    @Query("SELECT p FROM Payment p JOIN p.order o WHERE p.id = :paymentId AND o.userId = :userId")
    Optional<Payment> findByUserIdAndId(Long userId, Long paymentId);
}
