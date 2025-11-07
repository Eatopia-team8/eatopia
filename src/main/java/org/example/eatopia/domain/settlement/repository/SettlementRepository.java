package org.example.eatopia.domain.settlement.repository;

import org.example.eatopia.domain.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}