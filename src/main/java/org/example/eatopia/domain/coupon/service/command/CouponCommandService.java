package org.example.eatopia.domain.coupon.service.command;

import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;

public interface CouponCommandService {

    CouponResponse createCoupon(CouponCreateRequest request, UserPrincipal userAuth);

    void downloadCoupon(UserPrincipal authUser, Long couponId);
}
