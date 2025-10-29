package org.example.eatopia.domain.refund.controller;

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

@RestController
@RequestMapping("/v2/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundCommandService refundCommandService;
    private final RefundQueryService refundQueryService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Response<RefundResponse>> requestRefund(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody RefundCreateRequest request
    ) {
        RefundResponse refundResponse = refundCommandService.requestRefund(authUser.getId(), request);
        return ResponseEntity.ok(Response.success(refundResponse));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Response<Page<RefundResponse>>> getMyRefunds(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        Page<RefundResponse> responsePage = refundQueryService.getRefunds(authUser.getId(), pageable);
        return ResponseEntity.ok(Response.success(responsePage));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{refundId}/success")
    public ResponseEntity<Response<RefundResponse>> successRefund(
            @PathVariable Long refundId
    ) {
        RefundResponse refundResponse = refundCommandService.successRefund(refundId);
        return ResponseEntity.ok(Response.success(refundResponse));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{refundId}/canceled")
    public ResponseEntity<Response<RefundResponse>> canceledRefund(
            @PathVariable Long refundId
    ) {
        RefundResponse refundResponse = refundCommandService.canceledRefund(refundId);
        return ResponseEntity.ok(Response.success(refundResponse));
    }
}