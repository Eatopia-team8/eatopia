package org.example.eatopia.domain.search.dto.response;


public record SearchKeywordRankResponse(
        int rank,
        String keyword,
        long searchCount
) {
}