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

        List<SaleResponse> filteredSales;
        Long sellerId = request.sellerId();

        if (sellerId == null) {
            filteredSales = List.of();

        } else {
            if ("monthly".equalsIgnoreCase(request.period())) {
                List<String> periods = getPeriods(request.startDate(), request.endDate(), request.period());

                filteredSales = periods.stream()
                        .map(yearMonth -> redisStatisticRepository.getMonthlySellerSalesBySeller(sellerId, yearMonth))
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .toList();
            } else {
                filteredSales = redisStatisticRepository.getDailySellerSalesBySeller(
                        sellerId,
                        request.startDate(),
                        request.endDate()
                );
            }
        }

        int totalElements = filteredSales.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);

        if (start > end) {
            return new PageImpl<>(List.of(), pageable, totalElements);
        }

        List<SaleResponse> pageContent = filteredSales.subList(start, end);
        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    @Override
    public TotalSaleSummaryResponse getTotalSales(SaleSearchRequest request) {
        statisticValidator.validateSearchRequestDates(request);

        List<PeriodSaleResponse> periodSales;

        if ("monthly".equalsIgnoreCase(request.period())) {
            List<String> periods = getPeriods(request.startDate(), request.endDate(), request.period());
            periodSales = periods.stream()
                    .map(redisStatisticRepository::getMonthlyTotalSales)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .toList();
        } else {
            // ZRANGEBYSCORE
            periodSales = redisStatisticRepository.getDailyTotalSales(request.startDate(), request.endDate());
        }

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