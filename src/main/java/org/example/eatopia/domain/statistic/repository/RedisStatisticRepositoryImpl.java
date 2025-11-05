package org.example.eatopia.domain.statistic.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisStatisticRepositoryImpl implements RedisStatisticRepository {

    private static final String TOP_SELLER_KEY = "top_seller_rank";
    private static final String STAT_SELLER_DAILY_KEY = "statistics:sales:seller:daily_zset";
    private static final String STAT_TOTAL_DAILY_KEY = "statistics:sales:total:daily_zset";
    private static final String STAT_SELLER_MONTHLY_KEY = "statistics:sales:seller:monthly:";
    private static final String STAT_TOTAL_MONTHLY_KEY = "statistics:sales:total:monthly:";
    private static final Duration STAT_TTL = Duration.ofDays(31); // 데이터 보관 기간

    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, String> myStringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getTopSellers(int limit) {
        return objectRedisTemplate
                .opsForZSet()
                .reverseRangeWithScores(TOP_SELLER_KEY, 0, limit - 1);
    }

    @Override
    public void saveDailySellerSales(LocalDate date, List<SaleResponse> sales) {
        String key = STAT_SELLER_DAILY_KEY;
        if (sales == null || sales.isEmpty()) return;

        double score = date.toEpochDay(); // 날짜를 score로 사용
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (SaleResponse sale : sales) {
            try {
                String value = objectMapper.writeValueAsString(sale); // 객체를 JSON 문자열로 직렬화
                tuples.add(ZSetOperations.TypedTuple.of(value, score));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize SaleResponse: {}", sale, e);
            }
        }
        if (!tuples.isEmpty()) {
            myStringRedisTemplate.opsForZSet().add(key, tuples);
            myStringRedisTemplate.expire(key, STAT_TTL);
        }
    }

    @Override
    public List<SaleResponse> getDailySellerSales(LocalDate startDate, LocalDate endDate) {
        String key = STAT_SELLER_DAILY_KEY;
        double minScore = startDate.toEpochDay();
        double maxScore = endDate.toEpochDay();

        // ZRANGEBYSCORE로 범위 조회
        Set<String> jsonValues = myStringRedisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);

        if (jsonValues == null || jsonValues.isEmpty()) {
            return List.of();
        }

        // JSON 문자열을 SaleResponse 객체로 역직렬화
        return jsonValues.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, SaleResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize SaleResponse json: {}", json, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void saveDailyTotalSales(LocalDate date, List<PeriodSaleResponse> sales) {
        String key = STAT_TOTAL_DAILY_KEY;
        if (sales == null || sales.isEmpty()) return;

        double score = date.toEpochDay();
        Set<ZSetOperations.TypedTuple<String>> tuples = new HashSet<>();
        for (PeriodSaleResponse sale : sales) {
            try {
                String value = objectMapper.writeValueAsString(sale);
                tuples.add(ZSetOperations.TypedTuple.of(value, score));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize PeriodSaleResponse: {}", sale, e);
            }
        }
        if (!tuples.isEmpty()) {
            myStringRedisTemplate.opsForZSet().add(key, tuples);
            myStringRedisTemplate.expire(key, STAT_TTL);
        }
    }

    @Override
    public List<PeriodSaleResponse> getDailyTotalSales(LocalDate startDate, LocalDate endDate) {
        String key = STAT_TOTAL_DAILY_KEY;
        double minScore = startDate.toEpochDay();
        double maxScore = endDate.toEpochDay();

        Set<String> jsonValues = myStringRedisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);

        if (jsonValues == null || jsonValues.isEmpty()) {
            return List.of();
        }

        return jsonValues.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, PeriodSaleResponse.class);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to deserialize PeriodSaleResponse json: {}", json, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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