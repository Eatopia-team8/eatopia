package org.example.eatopia.domain.statistic.service;

import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.SaleRankingResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.example.eatopia.domain.statistic.dto.response.TotalSaleSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RedisStatisticQueryService {

    Page<SaleResponse> getSellerSales(SaleSearchRequest request, Pageable pageable);

    TotalSaleSummaryResponse getTotalSales(SaleSearchRequest request);

    List<SaleRankingResponse> getTopSellers(int limit);
}