package org.example.eatopia.domain.coupon.repository;

import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}