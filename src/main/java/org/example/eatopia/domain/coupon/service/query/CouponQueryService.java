package org.example.eatopia.domain.coupon.service.query;

import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryService {

    CouponResponse getCoupon(Long couponId);

    Page<CouponResponse> getCreatedCoupons(Pageable pageable);
}