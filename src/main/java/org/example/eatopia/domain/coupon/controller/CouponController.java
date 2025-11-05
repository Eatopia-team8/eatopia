package org.example.eatopia.domain.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Coupon API", description = "쿠폰 관련 API")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryService couponQueryService;
    private final CouponCommandService couponCommandService;

    @Operation(summary = "쿠폰 생성", description = "관리자 또는 판매자가 쿠폰을 생성합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "쿠폰 생성 실패")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @PostMapping("/v1/coupons")
    public Response<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request,
                                                 @AuthenticationPrincipal UserPrincipal userAuth) {

        CouponResponse response = couponCommandService.createCoupon(request, userAuth);

        return Response.success(response);
    }

    @Operation(summary = "쿠폰 다운로드", description = "구매자가 다운로드 가능한 쿠폰을 발급받습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
                    @ApiResponse(responseCode = "400", description = "쿠폰 발급 실패")
            })
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    @PostMapping("/v1/coupons/{couponId}/download")
    public Response<Void> downloadCoupon(@AuthenticationPrincipal UserPrincipal authUser,
                                         @PathVariable Long couponId) {

        couponCommandService.downloadCoupon(authUser, couponId);

        return Response.success();
    }

    @Operation(summary = "쿠폰 상세 조회", description = "쿠폰의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 쿠폰")
            })
    @GetMapping("/v1/coupons/{couponId}")
    public Response<CouponResponse> getCoupon(@PathVariable Long couponId) {

        CouponResponse response = couponQueryService.getCoupon(couponId);

        return Response.success(response);
    }

    @Operation(summary = "발급받은 쿠폰 목록 조회", description = "구매자가 본인이 발급받은 쿠폰 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            })
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    @GetMapping("/v1/coupons/issued")
    public Response<Page<CouponResponse>> getIssuedCoupons(@AuthenticationPrincipal UserPrincipal authUser,
                                                           @PageableDefault(size = 30, sort = "issuedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CouponResponse> response = couponQueryService.getIssuedCoupons(authUser, pageable);

        return Response.success(response);
    }

    @Operation(summary = "전체 쿠폰 목록 조회", description = "관리자가 생성된 모든 쿠폰 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/v1/all-coupons")
    public Response<Page<CouponResponse>> getCreatedCoupons(@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CouponResponse> responses = couponQueryService.getCreatedCoupons(pageable);

        return Response.success(responses);
    }

    @Operation(summary = "내가 생성한 쿠폰 목록 조회", description = "관리자 또는 판매자가 자신이 생성한 쿠폰 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping("/v1/coupons/created")
    public Response<Page<CouponResponse>> getCreatedCouponsByMe(@AuthenticationPrincipal UserPrincipal userAuth,
                                                                @PageableDefault(size = 30, sort = "startAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CouponResponse> responses = couponQueryService.getCreatedCouponsByMe(userAuth, pageable);

        return Response.success(responses);
    }

    // TODO: 현재 ADMIN의 생성된 모든 쿠폰 조회와 로직이 동일합니다. 추후 특정 유저가 어떤 쿠폰을 선별 조회 가능할지에 대한 정책적인 논의를 적용하여 로직을 수정해야할 것 같습니다.
    @Operation(summary = "다운로드 가능한 쿠폰 목록 조회", description = "구매자가 현재 다운로드 가능한 쿠폰 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            })
    @PreAuthorize("hasAuthority('ROLE_BUYER')")
    @GetMapping("/v1/coupons/downloadable")
    public Response<Page<CouponResponse>> getDownloadableCoupons(@AuthenticationPrincipal UserPrincipal userAuth,
                                                                 @PageableDefault(size = 30, sort = "startAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<CouponResponse> responses = couponQueryService.getDownloadableCoupons(userAuth, pageable);

        return Response.success(responses);
    }

    @Operation(summary = "쿠폰 삭제", description = "관리자 또는 판매자가 쿠폰을 삭제합니다. (소프트 삭제)",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "삭제 실패")
            })
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @DeleteMapping("/v1/coupons/{couponId}")
    public Response<Void> deleteCoupon(@AuthenticationPrincipal UserPrincipal userAuth,
                                       @PathVariable Long couponId) {

        couponCommandService.deleteCoupon(userAuth, couponId);

        return Response.success();
    }
}