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
    NotFoundCouponId(HttpStatus.NOT_FOUND, "CPN-006", "해당 쿠폰을 찾을 수 없습니다."),
    DEACTIVATED_COUPON(HttpStatus.BAD_REQUEST, "CPN-007", "비활성화 상태인 쿠폰입니다."),
    INVALID_DOWNLOAD_DATE(HttpStatus.BAD_REQUEST, "CPN-008", "쿠폰 다운로드 기간이 아닙니다."),
    SOLD_OUT_COUPON(HttpStatus.GONE, "CPN-009", "쿠폰 재고가 모두 소진되었습니다."),
    INVALID_COUPON(HttpStatus.NOT_FOUND, "CPN-010", "해당 쿠폰을 찾을 수 없습니다."),
    DUPLICATE_COUPON_ISSUE(HttpStatus.CONFLICT, "CPN-011", "이미 발급받은 쿠폰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}