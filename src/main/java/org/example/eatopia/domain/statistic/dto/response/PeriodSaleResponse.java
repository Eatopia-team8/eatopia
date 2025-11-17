package org.example.eatopia.domain.statistic.dto.response;

import java.math.BigDecimal;

/**
 * 기간별 매출 조회
 */
public record PeriodSaleResponse(
        String period,
        BigDecimal totalAmount
) {

}
