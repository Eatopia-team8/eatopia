package org.example.eatopia.domain.statistic.dto.response;

import java.math.BigDecimal;

/**
 * 판매자별 매출 조회
 */
public record SaleResponse(
        String period,
        Long sellerId,
        String sellerName,
        BigDecimal totalSaleAmount
) {

}