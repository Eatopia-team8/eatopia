package org.example.eatopia.domain.statistic.validator;

import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.exception.StatisticErrorCode;
import org.example.eatopia.domain.statistic.exception.StatisticException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StatisticValidator {

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
        }
    }
}
