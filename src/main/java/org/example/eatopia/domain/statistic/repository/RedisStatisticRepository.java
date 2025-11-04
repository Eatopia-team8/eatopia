package org.example.eatopia.domain.statistic.repository;

import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface RedisStatisticRepository {
    /**
     * 상위 n명의 판매자 내림차순으로 조회
     */
    Set<ZSetOperations.TypedTuple<Object>> getTopSellers(int limit);

    /**
     * save        : 저장
     * get         : 조회
     * SellerSales : 판매자별 매출
     * TotalSales  : 기간별 총매출
     * Daily       : 일별
     * Monthly     : 월별
     */
    void saveDailySellerSales(LocalDate date, List<SaleResponse> sales);

    List<SaleResponse> getDailySellerSales(LocalDate date);

    void saveDailyTotalSales(LocalDate date, List<PeriodSaleResponse> sales);

    List<PeriodSaleResponse> getDailyTotalSales(LocalDate date);

    void saveMonthlySellerSales(String yearMonth, List<SaleResponse> sales);

    List<SaleResponse> getMonthlySellerSales(String yearMonth);

    void saveMonthlyTotalSales(String yearMonth, List<PeriodSaleResponse> sales);

    List<PeriodSaleResponse> getMonthlyTotalSales(String yearMonth);
}