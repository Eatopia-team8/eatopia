package org.example.eatopia.domain.statistics.dto.response;

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
