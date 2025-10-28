package org.example.eatopia.domain.statistic.dto.response;

import java.util.List;

/**
 * 전체 매출
 */
public record TotalSaleSummaryResponse(
        List<PeriodSaleResponse> periodSales,
        List<SellerSaleRankingResponse> topSellers
) {

}
