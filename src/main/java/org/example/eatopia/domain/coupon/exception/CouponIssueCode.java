package org.example.eatopia.domain.coupon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponIssueCode implements ErrorCode {

    COUPON_ISSUE_NOT_FOUND(HttpStatus.NOT_FOUND, "CPNI-001", "발급된 쿠폰을 찾을 수 없습니다."),
    INVALID_DISCOUNT_RATE(HttpStatus.BAD_REQUEST, "CPNI-002", "허용된 할인율 범위를 벗어났습니다."),
    COUPON_ALREADY_USED(HttpStatus.CONFLICT, "CPNI-003", "이미 사용된 쿠폰입니다."),
    COUPON_NOT_YET_VALID(HttpStatus.BAD_REQUEST, "CPNI-005", "아직 사용 가능한 시기가 아닙니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "CPNI-006", "만료된 쿠폰입니다."),
    COUPON_INACTIVE(HttpStatus.BAD_REQUEST, "CPNI-007", "비활성화된(삭제된) 쿠폰입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
