package org.example.eatopia.domain.statistic.repository;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisStatisticRepositoryImpl implements RedisStatisticRepository {

    private static final String TOP_SELLER_KEY = "top_seller_rank";
    private static final String STAT_SELLER_DAILY_KEY = "statistics:sales:seller:daily:";
    private static final String STAT_TOTAL_DAILY_KEY = "statistics:sales:total:daily:";
    private static final String STAT_SELLER_MONTHLY_KEY = "statistics:sales:seller:monthly:";
    private static final String STAT_TOTAL_MONTHLY_KEY = "statistics:sales:total:monthly:";
    private static final Duration STAT_TTL = Duration.ofDays(31); // 데이터 보관 기간

    private final RedisTemplate<String, Object> objectRedisTemplate;

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getTopSellers(int limit) {
        return objectRedisTemplate
                .opsForZSet()
                .reverseRangeWithScores(TOP_SELLER_KEY, 0, limit - 1);
    }

    @Override
    public void saveDailySellerSales(LocalDate date, List<SaleResponse> sales) {
        String key = STAT_SELLER_DAILY_KEY + date.toString();
        objectRedisTemplate.opsForValue().set(key, sales, STAT_TTL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SaleResponse> getDailySellerSales(LocalDate date) {
        String key = STAT_SELLER_DAILY_KEY + date.toString();
        // JSON으로 직렬화된 List<SaleResponse>를 역직렬화
        return (List<SaleResponse>) objectRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveDailyTotalSales(LocalDate date, List<PeriodSaleResponse> sales) {
        String key = STAT_TOTAL_DAILY_KEY + date.toString();
        objectRedisTemplate.opsForValue().set(key, sales, STAT_TTL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PeriodSaleResponse> getDailyTotalSales(LocalDate date) {
        String key = STAT_TOTAL_DAILY_KEY + date.toString();
        return (List<PeriodSaleResponse>) objectRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveMonthlySellerSales(String yearMonth, List<SaleResponse> sales) {
        String key = STAT_SELLER_MONTHLY_KEY + yearMonth;
        objectRedisTemplate.opsForValue().set(key, sales, STAT_TTL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SaleResponse> getMonthlySellerSales(String yearMonth) {
        String key = STAT_SELLER_MONTHLY_KEY + yearMonth;
        return (List<SaleResponse>) objectRedisTemplate.opsForValue().get(key);
    }

    @Override
    public void saveMonthlyTotalSales(String yearMonth, List<PeriodSaleResponse> sales) {
        String key = STAT_TOTAL_MONTHLY_KEY + yearMonth;
        objectRedisTemplate.opsForValue().set(key, sales, STAT_TTL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PeriodSaleResponse> getMonthlyTotalSales(String yearMonth) {
        String key = STAT_TOTAL_MONTHLY_KEY + yearMonth;
        return (List<PeriodSaleResponse>) objectRedisTemplate.opsForValue().get(key);
    }
}