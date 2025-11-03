package org.example.eatopia.domain.search.service.query;

import org.example.eatopia.domain.search.dto.response.SearchKeywordRankResponse;

import java.util.List;

public interface SearchKeywordQueryService {
    void recordKeyword(String keyword);

    List<SearchKeywordRankResponse> getTopKeywords(int limit);
}