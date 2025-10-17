package org.example.eatopia.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.service.command.CouponCommandServiceImpl;
import org.example.eatopia.domain.coupon.service.query.CouponQueryServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryServiceImpl couponQueryServiceImpl;
    private final CouponCommandServiceImpl couponCommandServiceImpl;

    @PostMapping("/v1/coupons")
    public Response<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request,
                                                 @AuthenticationPrincipal UserDetails userAuth) {

        CouponResponse response = couponCommandServiceImpl.createCoupon(request);

        return Response.success(response);
    }
}