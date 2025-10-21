package org.example.eatopia.domain.coupon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {

    // ===== [VALIDATION] 입력/요청 값 유효성 오류 =====
    ILLEGAL_END_DATE(HttpStatus.BAD_REQUEST, "CPN-001", "종료일은 시작일 이후여야 합니다."),
    INVALID_PERCENT_RANGE(HttpStatus.BAD_REQUEST, "CPN-002", "퍼센트 할인일 경우 0~100 사이여야 합니다."),
    INVALID_TOTAL_QUANTITY(HttpStatus.BAD_REQUEST, "CPN-003", "총 발급 수량은 0 이상이어야 합니다."),
    PAST_START_DATE(HttpStatus.BAD_REQUEST, "CPN-004", "쿠폰 시작일은 현재 시각 이후여야 합니다."),
    INVALID_MIN_ORDER_AMOUNT(HttpStatus.BAD_REQUEST, "CPN-005", "최소 주문 금액은 할인 금액보다 커야 합니다."),

    // ===== [TIME_WINDOW] 기간/시간 제약으로 인한 불가 =====
    INVALID_DOWNLOAD_DATE(HttpStatus.FORBIDDEN, "CPN-008", "쿠폰 다운로드 기간이 아닙니다."),
    NOT_STARTED_COUPON(HttpStatus.FORBIDDEN, "CPN-013", "쿠폰 사용 가능기간이 아닙니다."),
    EXPIRED_COUPON(HttpStatus.GONE, "CPN-014", "이미 만료된 쿠폰입니다."),

    // ===== [RESOURCE_STATE] 재고/상태로 인한 불가 =====
    SOLD_OUT_COUPON(HttpStatus.GONE, "CPN-009", "쿠폰 재고가 모두 소진되었습니다."),

    // ===== [NOT_FOUND] 조회 결과 없음/비공개 =====
    INVALID_COUPON(HttpStatus.NOT_FOUND, "CPN-010", "해당 쿠폰을 찾을 수 없습니다."),
    DELETED_COUPON(HttpStatus.NOT_FOUND, "CPN-012", "삭제된 쿠폰입니다."),

    // ===== [CONFLICT] 상태 충돌/중복 =====
    DUPLICATE_COUPON_ISSUE(HttpStatus.CONFLICT, "CPN-011", "이미 발급받은 쿠폰입니다."),

    // ===== [ELIGIBILITY] 자격/권한 부족 =====
    ONLY_FOR_NEW_USER(HttpStatus.FORBIDDEN, "CPN-015", "신규가입 유저를 위한 쿠폰입니다."),
    ONLY_FOR_FIRST_ORDER(HttpStatus.FORBIDDEN, "CPN-016", "첫 주문 전용 쿠폰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}