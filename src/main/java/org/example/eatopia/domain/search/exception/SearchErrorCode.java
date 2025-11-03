package org.example.eatopia.domain.search.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {
    SEARCH_INVALID_LIMIT(HttpStatus.BAD_REQUEST, "SRH-001", "검색어 조회 개수는 1개 이상 100개 이하여야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
