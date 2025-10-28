package org.example.eatopia.domain.statistic.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StatisticErrorCode implements ErrorCode {

    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "STA-001", "조회 시작일은 종료일보다 늦을 수 없습니다."),
    MISSING_DATE_PARAMETER(HttpStatus.BAD_REQUEST, "STA-002", "기간 조회를 위해서는 시작일과 종료일이 필요합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}