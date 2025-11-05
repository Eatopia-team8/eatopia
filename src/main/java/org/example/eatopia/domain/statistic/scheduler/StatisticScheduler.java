package org.example.eatopia.domain.statistic.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.example.eatopia.domain.statistic.repository.RedisStatisticRepository;
import org.example.eatopia.domain.statistic.repository.StatisticRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatisticScheduler {

    private static final DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private final StatisticRepository statisticRepository;
    private final RedisStatisticRepository redisStatisticRepository;

    /**
     * https://www.econovill.com/news/articleView.html?idxno=559486
     * 매일 5시에 하루치의 일별/월별 통계를 계산하여 Redis에 저장
     * db 부하로 인하여 사용자가 적게 사용하는 시간에 처리
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void aggregateDailySales() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        aggregateSales("daily", targetDate, targetDate);
    }

    /**
     * 매월 1일 5시 10분에 지난달 전체의 월별 통계를 계산하여 Redis에 저장
     */
    @Scheduled(cron = "0 10 5 1 * ?")
    public void aggregateMonthlySales() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfPreviousMonth = today.minusDays(1);
        LocalDate firstDayOfPreviousMonth = lastDayOfPreviousMonth.withDayOfMonth(1);
        aggregateSales("monthly", firstDayOfPreviousMonth, lastDayOfPreviousMonth);
    }

    private void aggregateSales(String period, LocalDate startDate, LocalDate endDate) {

        SaleSearchRequest request = new SaleSearchRequest(
                null,
                period,
                startDate,
                endDate
        );

        // DB에서 판매자별 매출 집계
        List<SaleResponse> sellerSales = statisticRepository
                .findSellerSaleByPeriod(request, Pageable.unpaged())
                .getContent();

        // DB에서 기간별 총매출 집계
        List<PeriodSaleResponse> totalSales = statisticRepository
                .findTotalSaleByPeriod(request);

        //저장
        if ("monthly".equalsIgnoreCase(period)) {
            String yearMonth = startDate.format(YYYY_MM_FORMATTER);
            redisStatisticRepository.saveMonthlySellerSales(yearMonth, sellerSales);
            redisStatisticRepository.saveMonthlyTotalSales(yearMonth, totalSales);
        } else {
            redisStatisticRepository.saveDailySellerSales(startDate, sellerSales);
            redisStatisticRepository.saveDailyTotalSales(startDate, totalSales);
        }
    }
}