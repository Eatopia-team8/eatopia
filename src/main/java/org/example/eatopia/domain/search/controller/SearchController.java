package org.example.eatopia.domain.search.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.search.dto.response.SearchKeywordRankResponse;
import org.example.eatopia.domain.search.service.query.SearchKeywordQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchKeywordQueryService searchKeywordQueryService;

    // 인기 검색어 조회 (기본 10개)
    @GetMapping("/v1/search/keywords/popular")
    public ResponseEntity<Response<List<SearchKeywordRankResponse>>> getPopularKeywords(@RequestParam(defaultValue = "10") int limit) {

        List<SearchKeywordRankResponse> response = searchKeywordQueryService.getTopKeywords(limit);

        return ResponseEntity.ok(Response.success(response));
    }
}
