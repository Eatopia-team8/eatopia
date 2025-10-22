package org.example.eatopia.domain.payment.repository;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    Optional<Payment> findByOrderUserIdAndId(Long userId, Long paymentId);
}
