package org.example.eatopia.domain.coupon.service.command;

import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;

public interface CouponCommandService {

    CouponResponse createCoupon(CouponCreateRequest request);
}
