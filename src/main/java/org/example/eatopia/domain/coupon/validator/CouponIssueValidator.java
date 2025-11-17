package org.example.eatopia.domain.coupon.validator;

import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.coupon.entity.CouponIssue;
import org.example.eatopia.domain.coupon.exception.CouponErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponException;
import org.example.eatopia.domain.coupon.exception.CouponIssueErrorCode;
import org.example.eatopia.domain.coupon.exception.CouponIssueException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class CouponIssueValidator {

    // 최소 결제 금액 검증
    public void validateMinOrderAmount(BigDecimal minOrderAmount, BigDecimal currentOrderAmount) {
        if (minOrderAmount != null && currentOrderAmount.compareTo(minOrderAmount) < 0) {
            throw new CouponException(CouponErrorCode.INVALID_MIN_ORDER_AMOUNT);
        }
    }

    // 할인율 0~100 검증
    public void validateDiscountPercentRange(BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new CouponIssueException(CouponIssueErrorCode.INVALID_DISCOUNT_RATE);
        }
    }

    // 사용 가능한 쿠폰인지 검증
    public void validateUsable(CouponIssue couponIssue) {

        Coupon coupon = couponIssue.getCoupon();
        LocalDateTime now = LocalDateTime.now();

        // 이미 사용된 쿠폰 검증
        if (couponIssue.getUsedAt() != null) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_ALREADY_USED);
        }

        // 쿠폰 유효기간 검증
        if (coupon.getStartAt().isAfter(now)) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_NOT_YET_VALID);
        }
        if (coupon.getEndAt().isBefore(now)) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_EXPIRED);
        }

        // 삭제된 쿠폰 검증
        if (coupon.getDeletedAt() != null) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_INACTIVE);
        }
    }

    public void validateRollbackable(CouponIssue couponIssue) {

        Coupon coupon = couponIssue.getCoupon();
        LocalDateTime now = LocalDateTime.now();

        if (couponIssue.getUsedAt() == null) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_NOT_USED_YET);
        }

        // 이미 만료된 쿠폰은 롤백 불가
        if (coupon.getEndAt().isBefore(now)) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_EXPIRED);
        }

        // 삭제된 쿠폰은 롤백 불가
        if (coupon.getDeletedAt() != null) {
            throw new CouponIssueException(CouponIssueErrorCode.COUPON_INACTIVE);
        }
    }
}
