package org.example.eatopia.domain.statistic.dto.response;

import java.math.BigDecimal;

/**
 * 판매자 랭킹
 */
public record SellerSaleRankingResponse(
        Long sellerId,
        String sellerName,
        BigDecimal totalAmount
) {

}
