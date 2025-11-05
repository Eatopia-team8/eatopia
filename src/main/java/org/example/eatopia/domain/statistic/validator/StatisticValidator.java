package org.example.eatopia.domain.statistic.validator;

import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.exception.StatisticErrorCode;
import org.example.eatopia.domain.statistic.exception.StatisticException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class StatisticValidator {
    private static final long MAX_DAILY_QUERY_DAYS = 365;

    /**
     * 시작일이 종료일보다 늦은 경우
     */
    public void validateSearchRequestDates(SaleSearchRequest request) {
        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new StatisticException(StatisticErrorCode.INVALID_DATE_RANGE);
            }

            if ("daily".equalsIgnoreCase(request.period())) {
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (daysBetween > MAX_DAILY_QUERY_DAYS) {
                    throw new StatisticException(StatisticErrorCode.MAX_DATE_RANGE_EXCEEDED);
                }
            }
        } else if (startDate != null || endDate != null) {
            throw new StatisticException(StatisticErrorCode.MISSING_DATE_PARAMETER);
        }
    }
}
