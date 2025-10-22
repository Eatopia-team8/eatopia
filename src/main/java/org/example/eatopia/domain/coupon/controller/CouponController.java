package org.example.eatopia.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.dto.response.CouponResponse;
import org.example.eatopia.domain.coupon.service.command.CouponCommandService;
import org.example.eatopia.domain.coupon.service.query.CouponQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryService couponQueryService;
    private final CouponCommandService couponCommandService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @PostMapping("/v1/manager/coupons")
    public Response<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request,
                                                 @AuthenticationPrincipal UserPrincipal userAuth) {

        CouponResponse response = couponCommandService.createCoupon(request, userAuth);

        return Response.success(response);
    }

    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    @PostMapping("/v1/buyer/coupons/{couponId}/download")
    public Response<Void> downloadCoupon(@AuthenticationPrincipal UserPrincipal authUser,
                                         @PathVariable Long couponId) {

        couponCommandService.downloadCoupon(authUser, couponId);

        return Response.success();
    }

    @GetMapping("/v1/coupons/{couponId}")
    public Response<CouponResponse> getCoupon(@PathVariable Long couponId) {

        CouponResponse response = couponQueryService.getCoupon(couponId);

        return Response.success(response);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/v1/admin/coupons")
    public Response<Page<CouponResponse>> getCreatedCoupons(@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CouponResponse> responses = couponQueryService.getCreatedCoupons(pageable);

        return Response.success(responses);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping("/v1/manager/coupons/created")
    public Response<Page<CouponResponse>> getCreatedCouponsByMe(@AuthenticationPrincipal UserPrincipal userAuth,
                                                                @PageableDefault(size = 30, sort = "startAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CouponResponse> responses = couponQueryService.getCreatedCouponsByMe(userAuth, pageable);

        return Response.success(responses);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @DeleteMapping("/v1/manager/coupons/{couponId}/delete")
    public Response<Void> deleteCoupon(@AuthenticationPrincipal UserPrincipal userAuth,
                                       @PathVariable Long couponId) {

        couponCommandService.deleteCoupon(userAuth, couponId);

        return Response.success();
    }
}