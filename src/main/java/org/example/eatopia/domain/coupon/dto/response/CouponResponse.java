package org.example.eatopia.domain.coupon.dto.response;

import org.example.eatopia.domain.coupon.entity.Coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        String code,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean isActive,
        Boolean isNewUserOnly,
        Boolean isFirstOrderOnly,
        Integer usageLimit,
        Integer totalQuantity,
        Integer issuedQuantity,
        Integer remainingQuantity,
        Boolean isPercent,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount) {


    public static CouponResponse from(Coupon coupon) {

        return new CouponResponse(
                coupon.getCode(),
                coupon.getName(),
                coupon.getDescription(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                coupon.getIsActive(),
                coupon.getIsNewUserOnly(),
                coupon.getIsFirstOrderOnly(),
                coupon.getUsageLimit(),
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                coupon.getRemainingQuantity(),
                coupon.getPercent(),
                coupon.getDiscountValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getMinOrderAmount()
        );
    }
}
