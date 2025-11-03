package org.example.eatopia.domain.search.validator;

import org.example.eatopia.domain.search.exception.SearchErrorCode;
import org.example.eatopia.domain.search.exception.SearchException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SearchKeywordValidator {

    private static final int MAX_KEYWORD_LENGTH = 100;
    private static final int MAX_LIMIT = 100;

    // 검색어 유효성 검증
    public boolean isValidKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return false;
        }

        // 길이 체크
        if (keyword.length() > MAX_KEYWORD_LENGTH) {
            return false;
        }

        // 공백만 있는 경우
        if (keyword.trim().isEmpty()) {
            return false;
        }

        return true;
    }

    // limit 유효성 검증
    public void validateLimit(int limit) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new SearchException(SearchErrorCode.SEARCH_INVALID_LIMIT);
        }
    }
}