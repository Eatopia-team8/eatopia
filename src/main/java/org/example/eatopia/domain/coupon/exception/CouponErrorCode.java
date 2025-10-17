package org.example.eatopia.domain.coupon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {

    IllegalEndDate(HttpStatus.BAD_REQUEST, "CPN-001", "종료일은 시작일 이후여야 합니다."),
    InvalidPercentRange(HttpStatus.BAD_REQUEST, "CPN-002", "퍼센트 할인일 경우 0~100 사이여야 합니다."),
    InvalidTotalQuantity(HttpStatus.BAD_REQUEST, "CPN-003", "총 발급 수량은 0 이상이어야 합니다."),
    PastStartDate(HttpStatus.BAD_REQUEST, "CPN-004", "쿠폰 시작일은 현재 시각 이후여야 합니다."),
    InvalidMinOrderAmount(HttpStatus.BAD_REQUEST, "CPN-005", "최소 주문 금액은 할인 금액보다 커야 합니다."),
    NotFoundCouponId(HttpStatus.NOT_FOUND, "CPN-006",  "");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}