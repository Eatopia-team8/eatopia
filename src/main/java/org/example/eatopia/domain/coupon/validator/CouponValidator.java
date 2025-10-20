package org.example.eatopia.domain.coupon.validator;

import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CouponValidator {

    public void couponCreateValidate(CouponCreateRequest request) {
        LocalDateTime startDate = request.startDate();
        LocalDateTime endDate = request.endDate();

        // 종료일이 시작일보다 앞서는지
        if (endDate.isBefore(startDate)) {
            throw new CouponException(CouponErrorCode.IllegalEndDate);
        }

        // 시작일이 현재 이전인지
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new CouponException(CouponErrorCode.PastStartDate);
        }

        // 퍼센트 범위 검증
        if (Boolean.TRUE.equals(request.isPercent())) {
            BigDecimal value = request.discountValue();
            if (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new CouponException(CouponErrorCode.InvalidPercentRange);
            }
        }

        // 금액형 할인 시 최소주문금액 < 할인금액 체크
        if (Boolean.FALSE.equals(request.isPercent())
                && request.minOrderAmount() != null
                && request.discountValue() != null
                && request.minOrderAmount().compareTo(request.discountValue()) < 0) {
            throw new CouponException(CouponErrorCode.InvalidMinOrderAmount);
        }

        // 수량 검증
        if (request.totalQuantity() != null && request.totalQuantity() < 0) {
            throw new CouponException(CouponErrorCode.InvalidTotalQuantity);
        }
    }

    public void validateDownloadable(Coupon coupon) {

        if (!coupon.getIsActive()) {
            throw new CouponException(CouponErrorCode.DEACTIVATED_COUPON);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(coupon.getEndDate())) {
            throw new CouponException(CouponErrorCode.INVALID_DOWNLOAD_DATE);
        }

        if (coupon.getTotalQuantity() != null && coupon.getRemainingQuantity() <= 0) {
            throw new CouponException(CouponErrorCode.SOLD_OUT_COUPON);
        }
    }
}

