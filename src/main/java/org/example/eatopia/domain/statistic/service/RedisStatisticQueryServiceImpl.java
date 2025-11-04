package org.example.eatopia.domain.statistic.service;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleRankingResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.example.eatopia.domain.statistic.dto.response.TotalSaleSummaryResponse;
import org.example.eatopia.domain.statistic.repository.RedisStatisticRepository;
import org.example.eatopia.domain.statistic.validator.StatisticValidator;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisStatisticQueryServiceImpl implements RedisStatisticQueryService {
    private static final int TOP_SELLER_LIMIT = 10;
    private static final DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private final RedisStatisticRepository redisStatisticRepository;
    private final StatisticValidator statisticValidator;
    private final UserQueryService userQueryService;

    @Override
    public Page<SaleResponse> getSellerSales(SaleSearchRequest request, Pageable pageable) {
        statisticValidator.validateSearchRequestDates(request);

        List<String> periods = getPeriods(request.startDate(), request.endDate(), request.period());

        // Redis에서 해당 기간의 SaleResponse 데이터를 조회하여 하나로 합침
        Stream<SaleResponse> allSalesStream;

        if ("monthly".equalsIgnoreCase(request.period())) {
            allSalesStream = periods.stream()
                    .map(redisStatisticRepository::getMonthlySellerSales)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream);
        } else {
            allSalesStream = periods.stream()
                    .map(dateStr -> redisStatisticRepository.getDailySellerSales(LocalDate.parse(dateStr)))
                    .filter(Objects::nonNull)
                    .flatMap(List::stream);
        }

        // 특정 판매자 ID로 필터링
        if (request.sellerId() != null) {
            allSalesStream = allSalesStream.filter(sale -> sale.sellerId().equals(request.sellerId()));
        }

        List<SaleResponse> allSales = allSalesStream.toList();

        // 페이지네이션
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allSales.size());

        if (start > end) {
            return new PageImpl<>(List.of(), pageable, allSales.size());
        }

        List<SaleResponse> pageContent = allSales.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allSales.size());
    }

    @Override
    public TotalSaleSummaryResponse getTotalSales(SaleSearchRequest request) {
        statisticValidator.validateSearchRequestDates(request);

        //기간별 매출
        List<String> periods = getPeriods(request.startDate(), request.endDate(), request.period());

        List<PeriodSaleResponse> periodSales;
        if ("monthly".equalsIgnoreCase(request.period())) {
            periodSales = periods.stream()
                    .map(redisStatisticRepository::getMonthlyTotalSales)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .toList();
        } else {
            periodSales = periods.stream()
                    .map(dateStr -> redisStatisticRepository.getDailyTotalSales(LocalDate.parse(dateStr)))
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .toList();
        }

        //판매자 랭킹
        List<SaleRankingResponse> topSellers = getTopSellers(TOP_SELLER_LIMIT);

        return TotalSaleSummaryResponse.create(periodSales, topSellers);
    }

    private List<String> getPeriods(LocalDate startDate, LocalDate endDate, String periodType) {
        List<String> periods = new ArrayList<>();

        if ("monthly".equalsIgnoreCase(periodType)) {
            LocalDate current = startDate.withDayOfMonth(1);
            LocalDate last = endDate.withDayOfMonth(1);
            while (!current.isAfter(last)) {
                periods.add(current.format(YYYY_MM_FORMATTER));
                current = current.plusMonths(1);
            }
        } else {
            LocalDate current = startDate;
            while (!current.isAfter(endDate)) {
                periods.add(current.toString()); // YYYY-MM-DD
                current = current.plusDays(1);
            }
        }
        return periods;
    }

    @Override
    public List<SaleRankingResponse> getTopSellers(int limit) {
        Set<ZSetOperations.TypedTuple<Object>> topTuples = redisStatisticRepository.getTopSellers(limit);

        if (topTuples == null || topTuples.isEmpty()) {
            return List.of();
        }

        List<Long> sellerIds = topTuples.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .map(Objects::toString)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 이름 매핑을 위해 DB 조회
        Map<Long, String> sellerNameMap = sellerIds.stream()
                .map(userQueryService::getUserEntityById)
                .collect(Collectors.toMap(User::getId, User::getName));


        return topTuples.stream()
                .filter(Objects::nonNull)
                .map(tuple -> {
                    Long sellerId = Long.valueOf(tuple.getValue().toString());
                    // DB에서 조회한 이름 사용
                    String sellerName = sellerNameMap.getOrDefault(sellerId, "Unknown Seller");
                    BigDecimal totalAmount = BigDecimal.valueOf(tuple.getScore());

                    return new SaleRankingResponse(sellerId, sellerName, totalAmount);
                })
                .collect(Collectors.toList());
    }
}