package org.example.eatopia.domain.coupon.service.query;

import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryService {

    CouponResponse getCoupon(Long couponId);

    Page<CouponResponse> getCreatedCoupons(Pageable pageable);

    Page<CouponResponse> getCreatedCouponsByMe(UserPrincipal userAuth, Pageable pageable);

    Page<CouponResponse> getDownloadableCoupons(UserPrincipal userAuth, Pageable pageable);

    // 타도메인에서 사용하는 메서드
    CouponIssue getUsableIssuedCoupon(Long couponIssueId);
}