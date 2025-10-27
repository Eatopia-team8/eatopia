package org.example.eatopia.domain.refund.repository;

import org.example.eatopia.domain.refund.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    boolean existsByOrderDetailId(Long orderDetailId);
    
    Page<Refund> findByUserId(Long userId, Pageable pageable);
}