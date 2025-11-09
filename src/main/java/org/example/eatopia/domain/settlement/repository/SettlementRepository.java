package org.example.eatopia.domain.settlement.repository;

import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller")
    List<Settlement> findAllWithSeller();

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller WHERE s.status = :status")
    List<Settlement> findByStatusWithSeller(SettlementStatus status);

    @Query(value = "SELECT s FROM Settlement s JOIN FETCH s.seller WHERE s.seller.id = :sellerId",
            countQuery = "SELECT COUNT(s) FROM Settlement s WHERE s.seller.id = :sellerId")
    Page<Settlement> findBySellerIdWithSeller(@Param("sellerId") Long sellerId, Pageable pageable);

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller WHERE s.id = :id AND s.seller.id = :sellerId")
    Optional<Settlement> findByIdAndSellerIdWithSeller(@Param("id") Long id, @Param("sellerId") Long sellerId);

    @Query(value = "SELECT s FROM Settlement s JOIN FETCH s.seller",
            countQuery = "SELECT COUNT(s) FROM Settlement s")
    Page<Settlement> findAllWithSeller(Pageable pageable);

    @Query("SELECT s FROM Settlement s JOIN FETCH s.seller WHERE s.id = :id")
    Optional<Settlement> findByIdWithSeller(@Param("id") Long id);
}