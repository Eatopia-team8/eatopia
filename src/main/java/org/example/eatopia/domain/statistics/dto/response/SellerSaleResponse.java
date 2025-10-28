package org.example.eatopia.domain.statistics.dto.response;

import java.math.BigDecimal;

/**
 * 판매자별 매출 조회
 */
public record SellerSaleResponse(
        String period,
        Long sellerId,
        String sellerName,
        BigDecimal totalSaleAmount
) {

}