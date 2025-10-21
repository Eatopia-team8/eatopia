package org.example.eatopia.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Coupon> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Optional<Coupon> findCouponById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findWithLockById(Long couponId);

    boolean existsByCode(String code);
}