package org.example.eatopia.domain.statistic.dto.response;

import java.util.List;

/**
 * 전체 매출
 */
public record TotalSaleSummaryResponse(
        List<PeriodSaleResponse> periodSales,
        List<SaleRankingResponse> topSellers
) {

    public static TotalSaleSummaryResponse create(List<PeriodSaleResponse> periodSales, List<SaleRankingResponse> topSellers) {
        return new TotalSaleSummaryResponse(periodSales, topSellers);
    }
}
