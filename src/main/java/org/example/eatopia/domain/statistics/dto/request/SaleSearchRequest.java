package org.example.eatopia.domain.statistics.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 공통 검색 조건
 */
public record SaleSearchRequest(
        Long sellerId,
        String period,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {
    // LocalDate를 LocalDateTime으로 변환 (쿼리용 시작 시간)
    public LocalDateTime getStartDateTime() {
        return startDate != null ? startDate.atStartOfDay() : null;
    }

    // LocalDate를 LocalDateTime으로 변환 (쿼리용 종료 시간)
    public LocalDateTime getEndDateTime() {
        // endDate는 해당 날짜의 마지막 시간까지 포함
        return endDate != null ? endDate.plusDays(1).atStartOfDay().minusNanos(1) : null;
    }
}