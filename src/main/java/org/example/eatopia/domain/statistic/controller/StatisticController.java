package org.example.eatopia.domain.statistic.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.example.eatopia.domain.statistic.dto.response.TotalSaleSummaryResponse;
import org.example.eatopia.domain.statistic.service.StatisticQueryService;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v2/statistic")
@RequiredArgsConstructor

public class StatisticController {
    private final StatisticQueryService statisticQueryService;

    /**
     * 판매자 매출 조회 (ADMIN: 전체 및 선택 조회, SELLER: 자신만 조회)
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<Response<Page<SaleResponse>>> getSellerSales(
            @AuthenticationPrincipal UserPrincipal authUser,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(sort = "period") Pageable pageable
    ) {

        Long querySellerId = sellerId;

        if (authUser.getUserRole() == UserRole.SELLER) {
            querySellerId = authUser.getId();
        }

        SaleSearchRequest request = new SaleSearchRequest(querySellerId, period, startDate, endDate);
        Page<SaleResponse> resultPage = statisticQueryService.getSellerSales(request, pageable);
        return ResponseEntity.ok(Response.success(resultPage));
    }

    /**
     * 전체 매출 조회
     */

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<Response<TotalSaleSummaryResponse>> getTotalSalesSummary(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        SaleSearchRequest request = new SaleSearchRequest(null, period, startDate, endDate);
        TotalSaleSummaryResponse summary = statisticQueryService.getTotalSales(request);
        return ResponseEntity.ok(Response.success(summary));
    }
}