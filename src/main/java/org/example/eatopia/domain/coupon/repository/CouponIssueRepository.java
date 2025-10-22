package org.example.eatopia.domain.coupon.repository;

import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query(value = "SELECT ci FROM CouponIssue ci " +
            "JOIN FETCH ci.coupon c " +
            "JOIN FETCH c.user u " +
            "WHERE ci.user.id = :userId",
            countQuery = "SELECT count(ci) FROM CouponIssue ci WHERE ci.user.id = :userId")
    Page<CouponIssue> findAllByUserId(Long userId, Pageable pageable);
}