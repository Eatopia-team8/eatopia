package org.example.eatopia.domain.search.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.search.dto.response.SearchKeywordRankResponse;
import org.example.eatopia.domain.search.validator.SearchKeywordValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordQueryServiceImpl implements SearchKeywordQueryService {

    private final RedisTemplate<String, String> myStringRedisTemplate;
    private final SearchKeywordValidator validator;

    // 검색어 집계
    public void recordKeyword(String keyword) {
        if (!validator.isValidKeyword(keyword)) {
            return;
        }

        String key = getTodayKeywordKey();
        myStringRedisTemplate.opsForZSet().incrementScore(key, keyword.toLowerCase(), 1);
        setExpireAtMidnight(key);
    }

    // 인기 검색어 조회
    public List<SearchKeywordRankResponse> getTopKeywords(int limit) {
        validator.validateLimit(limit);

        String key = getTodayKeywordKey();
        Set<ZSetOperations.TypedTuple<String>> topKeywordsWithScore =
                myStringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, limit - 1);

        if (topKeywordsWithScore == null || topKeywordsWithScore.isEmpty()) {
            return Collections.emptyList();
        }

        return buildRankResponse(topKeywordsWithScore);
    }

    private List<SearchKeywordRankResponse> buildRankResponse(
            Set<ZSetOperations.TypedTuple<String>> tuples) {
        List<SearchKeywordRankResponse> result = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            result.add(new SearchKeywordRankResponse(
                    rank++,
                    tuple.getValue(),
                    tuple.getScore() != null ? tuple.getScore().longValue() : 0L
            ));
        }
        return result;
    }

    private String getTodayKeywordKey() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "search:keyword:" + today;
    }

    private void setExpireAtMidnight(String key) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long secondsUntilMidnight = Duration.between(now, midnight).getSeconds();
        myStringRedisTemplate.expire(key, Duration.ofSeconds(secondsUntilMidnight));
    }
}
