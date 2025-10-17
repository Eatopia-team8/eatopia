package org.example.eatopia.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.service.command.CouponCommandService;
import org.example.eatopia.domain.coupon.service.query.CouponQueryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryService couponQueryService;
    private final CouponCommandService couponCommandService;

    @PostMapping("/v1/coupons")
    public Response<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request,
                                                 @AuthenticationPrincipal UserDetails userAuth) {

        CouponResponse response = couponCommandService.createCoupon(request);

        return Response.success(response);
    }

    @GetMapping("/v1/coupons/{couponId}")
    public Response<CouponResponse> getCoupon(@PathVariable Long couponId) {

        CouponResponse response = couponQueryService.getCoupon(couponId);

        return Response.success(response);
    }
}