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
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatisticScheduler {

    private final StatisticRepository statisticRepository;
    private final RedisStatisticRepository redisStatisticRepository;

    /**
     * https://www.econovill.com/news/articleView.html?idxno=559486
     * 매일 5시에 하루치의 일별/월별 통계를 계산하여 Redis에 저장
     * db 부하로 인하여 사용자가 적게 사용하는 시간에 처리
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void aggregateDailyAndMonthlySales() {
        // 어제 날짜
        LocalDate targetDate = LocalDate.now().minusDays(1);
        // 일별 통계 집계 및 저장
        aggregateSales("daily", targetDate);

        // 월별 통계 집계 및 저장
        aggregateSales("monthly", targetDate);
    }

    private void aggregateSales(String period, LocalDate targetDate) {

        SaleSearchRequest request = new SaleSearchRequest(
                null,
                period,
                targetDate,
                targetDate
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
            // "YYYY-MM" 형식
            String yearMonth = targetDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            redisStatisticRepository.saveMonthlySellerSales(yearMonth, sellerSales);
            redisStatisticRepository.saveMonthlyTotalSales(yearMonth, totalSales);
        } else {
            // "YYYY-MM-DD" 형식
            redisStatisticRepository.saveDailySellerSales(targetDate, sellerSales);
            redisStatisticRepository.saveDailyTotalSales(targetDate, totalSales);
        }
    }
}