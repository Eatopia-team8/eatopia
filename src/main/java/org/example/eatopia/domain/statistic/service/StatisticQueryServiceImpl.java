package org.example.eatopia.domain.statistic.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleRankingResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.example.eatopia.domain.statistic.dto.response.TotalSaleSummaryResponse;
import org.example.eatopia.domain.statistic.repository.StatisticRepository;
import org.example.eatopia.domain.statistic.validator.StatisticValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticQueryServiceImpl implements StatisticQueryService {
    private static final int TOP_SELLER_LIMIT = 10;
    private final StatisticRepository statisticRepository;
    private final StatisticValidator statisticValidator;

    @Override
    public Page<SaleResponse> getSellerSale(SaleSearchRequest request, Pageable pageable) {
        statisticValidator.validateSearchRequestDates(request);

        return statisticRepository.findSellerSaleByPeriod(request, pageable);
    }

    @Override
    public TotalSaleSummaryResponse getTotalSales(SaleSearchRequest request) {
        statisticValidator.validateSearchRequestDates(request);

        List<PeriodSaleResponse> periodSales = statisticRepository.findTotalSaleByPeriod(request);
        List<SaleRankingResponse> topSellers = statisticRepository.findTopSellingSeller(request, TOP_SELLER_LIMIT);

        return TotalSaleSummaryResponse.create(periodSales, topSellers);
    }
}
