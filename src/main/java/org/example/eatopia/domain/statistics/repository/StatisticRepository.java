package org.example.eatopia.domain.statistics.repository;

import org.example.eatopia.domain.statistics.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistics.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistics.dto.response.SellerSaleRankingResponse;
import org.example.eatopia.domain.statistics.dto.response.SellerSaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StatisticRepository {

    Page<SellerSaleResponse> findSellerSaleByPeriod(SaleSearchRequest condition, Pageable pageable);

    List<PeriodSaleResponse> findTotalSaleByPeriod(SaleSearchRequest condition);

    List<SellerSaleRankingResponse> findTopSellingSeller(SaleSearchRequest condition, int limit);
}