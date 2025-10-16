package org.example.eatopia.domain.payment.repository;

import org.example.eatopia.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
