package org.example.eatopia.domain.coupon.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponCreateRequest(

        @NotBlank(message = "쿠폰 이름은 필수입니다.")
        @Size(max = 50, message = "쿠폰 이름은 50자를 초과할 수 없습니다.")
        String name,

        @NotBlank(message = "쿠폰 설명은 필수입니다.")
        @Size(max = 255, message = "쿠폰 설명은 255자를 초과할 수 없습니다.")
        String description,

        @NotNull(message = "시작일은 필수입니다.")
        LocalDateTime startDate,

        @NotNull(message = "종료일은 필수입니다.")
        LocalDateTime endDate,

        Boolean isActive,

        Boolean isNewUserOnly,

        Boolean isFirstOrderOnly,

        @Min(value = 1, message = "최소 사용 가능 횟수는 1회 이상이어야 합니다.")
        Integer usageLimit,

        @Min(value = 0, message = "총 발급 수량은 0 이상이어야 합니다.")
        Integer totalQuantity,

        Integer issuedQuantity,

        Integer remainingQuantity,

        @NotNull(message = "할인 형태는 필수입니다.")
        Boolean isPercent,

        @NotNull(message = "할인 값은 필수입니다.")
        @DecimalMin(value = "0", message = "할인 값은 0 이상이어야 합니다.")
        BigDecimal discountValue,

        @DecimalMin(value = "0", message = "최대 할인 금액은 0 이상이어야 합니다.")
        BigDecimal maxDiscountAmount,

        @DecimalMin(value = "0", message = "최소 결제 금액은 0 이상이어야 합니다.")
        BigDecimal minOrderAmount
) {

    public CouponCreateRequest {

        // 기본값 설정
        // 시작일이 현재 이후면 아직 비활성화
        if (isActive == null) {
            isActive = !startDate.isAfter(LocalDateTime.now());
        }

        // 신규회원/첫주문 여부
        if (isNewUserOnly == null) {
            isNewUserOnly = false;
        }
        if (isFirstOrderOnly == null) {
            isFirstOrderOnly = false;
        }

        // 사용 가능 횟수
        if (usageLimit == null) {
            usageLimit = 1;
        }

        // 수량 관련
        if (totalQuantity == null) {
            totalQuantity = 0;
        }
        remainingQuantity = totalQuantity;
    }
}