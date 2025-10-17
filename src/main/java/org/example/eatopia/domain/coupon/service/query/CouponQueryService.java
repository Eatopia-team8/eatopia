package org.example.eatopia.domain.coupon.service.query;

import org.example.eatopia.domain.coupon.dto.response.CouponResponse;

public interface CouponQueryService {
    CouponResponse getCoupon(Long couponId);
}
