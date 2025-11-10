package org.example.eatopia.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;
import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;
import org.example.eatopia.domain.settlement.service.command.SettlementCommandService;
import org.example.eatopia.domain.settlement.service.query.SettlementQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Settlement API", description = "정산 관리 API")
@RestController
@RequestMapping("/v1/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementCommandService settlementCommandService;
    private final SettlementQueryService settlementQueryService;

    @Operation(summary = "판매자 정산 요청 (비동기)",
            description = "ADMIN이 특정 판매자의 정산을 요청합니다. ",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/seller/{sellerId}")
    public ResponseEntity<Response<SettlementResponse>> createAndProcessSettlement(
            @PathVariable Long sellerId,
            @Valid @RequestBody SettlementCreateRequest request
    ) {
        SettlementResponse pendingResponse = settlementCommandService.requestSettlement(sellerId, request);
        settlementCommandService.processPayout(pendingResponse.settlementId(), request);

        return ResponseEntity.accepted().body(Response.success(pendingResponse));
    }

    @Operation(summary = "정산 내역 목록 조회",
            description = "ADMIN은 전체 정산 내역, SELLER는 자신의 정산 내역을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping
    public ResponseEntity<Response<Page<SettlementResponse>>> getSettlements(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SettlementResponse> response = settlementQueryService.getSettlements(authUser, pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "정산 내역 상세 조회",
            description = "ADMIN 또는 SELLER가 특정 정산 내역을 상세 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping("/{settlementId}")
    public ResponseEntity<Response<SettlementResponse>> getSettlement(
            @PathVariable Long settlementId,
            @AuthenticationPrincipal UserPrincipal authUser
    ) {
        SettlementResponse response = settlementQueryService.getSettlement(settlementId, authUser);
        return ResponseEntity.ok(Response.success(response));
    }
}