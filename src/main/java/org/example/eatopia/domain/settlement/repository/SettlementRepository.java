package org.example.eatopia.domain.settlement.repository;

import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller")
    List<Settlement> findAllWithSeller();

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller WHERE s.status = :status")
    List<Settlement> findByStatusWithSeller(SettlementStatus status);
}