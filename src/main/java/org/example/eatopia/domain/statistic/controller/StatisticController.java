package org.example.eatopia.domain.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Statistic API", description = "통계 관련 API")
@RestController
@RequestMapping("/v1/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticQueryService statisticQueryService;

    @Operation(summary = "판매자별 매출 조회",
            description = "ADMIN은 모든 판매자 또는 특정 판매자의 매출을 조회(일별,월별)할 수 있고, SELLER는 자신의 매출만 조회(일별,월별)할 수 있습니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "조회 실패")
            })
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

    @Operation(summary = "전체 매출 요약 조회",
            description = "지정된 기간 동안의 전체 매출(일별,월별) 목록과 매출 상위 판매자 10명의 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "조회 실패")
            })
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