package org.example.eatopia.domain.coupon.repository;

import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @EntityGraph(attributePaths = {"coupon", "coupon.user"})
    Page<CouponIssue> findAllByUserId(Long userId, Pageable pageable);
}