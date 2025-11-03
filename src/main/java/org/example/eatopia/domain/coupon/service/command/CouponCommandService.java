package org.example.eatopia.domain.coupon.service.command;

import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;

import java.math.BigDecimal;

public interface CouponCommandService {

    CouponResponse createCoupon(CouponCreateRequest request, UserPrincipal userAuth);

    void downloadCoupon(UserPrincipal authUser, Long couponId);

    void deleteCoupon(UserPrincipal userAuth, Long couponId);
    // 타 도메인에서 사용하는 메서드
    BigDecimal calculateDiscountValue(Long couponIssueId, BigDecimal totalProductPrice);
}
