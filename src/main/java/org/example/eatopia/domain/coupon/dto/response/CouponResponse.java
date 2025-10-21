package org.example.eatopia.domain.coupon.dto.response;

import org.example.eatopia.domain.coupon.entity.Coupon;
import org.example.eatopia.domain.user.dto.CouponCreatorInfoResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        CouponCreatorInfoResponse creator,
        String code,
        String name,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Boolean isNewUserOnly,
        Boolean isFirstOrderOnly,
        Integer usageLimit,
        Integer totalQuantity,
        Integer issuedQuantity,
        Integer remainingQuantity,
        Boolean isPercent,
        BigDecimal discountValue,
        BigDecimal maxDiscountAmount,
        BigDecimal minOrderAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CouponResponse of(Coupon coupon, CouponCreatorInfoResponse creator) {

        return new CouponResponse(
                coupon.getId(),
                creator,
                coupon.getCode(),
                coupon.getName(),
                coupon.getDescription(),
                coupon.getStartAt(),
                coupon.getEndAt(),
                coupon.getIsNewUserOnly(),
                coupon.getIsFirstOrderOnly(),
                coupon.getUsageLimit(),
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                coupon.getRemainingQuantity(),
                coupon.getPercent(),
                coupon.getDiscountValue(),
                coupon.getMaxDiscountAmount(),
                coupon.getMinOrderAmount(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt()
        );
    }
}
