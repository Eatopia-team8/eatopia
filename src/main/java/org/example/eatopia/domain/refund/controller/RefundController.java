package org.example.eatopia.domain.refund.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.refund.dto.request.RefundCreateRequest;
import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.example.eatopia.domain.refund.service.command.RefundCommandService;
import org.example.eatopia.domain.refund.service.query.RefundQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Refund API", description = "환불 API")
@RestController
@RequestMapping("/v2/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundCommandService refundCommandService;
    private final RefundQueryService refundQueryService;

    @Operation(summary = "환불 요청 생성", description = "BUYER가 환불을 요청합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "생성 실패")
            })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Response<RefundResponse>> requestRefund(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody RefundCreateRequest request
    ) {
        RefundResponse refundResponse = refundCommandService.requestRefund(authUser.getId(), request);
        return ResponseEntity.ok(Response.success(refundResponse));
    }

    @Operation(summary = "환불 목록 조회", description = "BUYER의 환불을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "조회 실패")
            })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Response<Page<RefundResponse>>> getMyRefunds(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<RefundResponse> responsePage = refundQueryService.getRefunds(authUser.getId(), pageable);
        return ResponseEntity.ok(Response.success(responsePage));
    }

    @Operation(summary = "환불 요청 승인", description = "ADMIN이 BUYER의 환불을 승인합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "승인 성공"),
                    @ApiResponse(responseCode = "400", description = "승인 실패")
            })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{refundId}/success")
    public ResponseEntity<Response<RefundResponse>> successRefund(
            @PathVariable Long refundId
    ) {
        RefundResponse refundResponse = refundCommandService.successRefund(refundId);
        return ResponseEntity.ok(Response.success(refundResponse));
    }

    @Operation(summary = "환불 요청 거절", description = "ADMIN이 BUYER의 환불을 거절합니다",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "거절 성공"),
                    @ApiResponse(responseCode = "400", description = "거절 실패")
            })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{refundId}/canceled")
    public ResponseEntity<Response<RefundResponse>> canceledRefund(
            @PathVariable Long refundId
    ) {
        RefundResponse refundResponse = refundCommandService.canceledRefund(refundId);
        return ResponseEntity.ok(Response.success(refundResponse));
    }
}