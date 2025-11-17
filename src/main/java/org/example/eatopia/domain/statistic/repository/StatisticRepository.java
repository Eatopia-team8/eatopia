package org.example.eatopia.domain.statistic.repository;

import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleRankingResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StatisticRepository {

    Page<SaleResponse> findSellerSaleByPeriod(SaleSearchRequest condition, Pageable pageable);

    List<PeriodSaleResponse> findTotalSaleByPeriod(SaleSearchRequest condition);

    List<SaleRankingResponse> findTopSellingSeller(SaleSearchRequest condition, int limit);
}