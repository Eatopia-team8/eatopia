package org.example.eatopia.domain.refund.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RefundErrorCode implements ErrorCode {
    ORDER_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "RFD-001", "환불할 주문 상품을 찾을 수 없습니다."),
    ALREADY_REFUNDED(HttpStatus.CONFLICT, "RFD-002", "이미 환불 처리된 상품입니다."),
    REFUND_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "RFD-003", "환불이 불가능한 주문 상태입니다."),
    ORDER_NOT_SUCCESSFUL(HttpStatus.BAD_REQUEST, "RFD-004", "결제가 완료되지 않은 주문은 환불할 수 없습니다."),
    REFUND_FORBIDDEN(HttpStatus.FORBIDDEN, "RFD-005", "해당 주문에 대한 환불 권한이 없습니다."),
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "RFD-006", "환불 요청 내역을 찾을 수 없습니다."),
    REFUND_NOT_PENDING(HttpStatus.BAD_REQUEST, "RFD-007", "환불 대기 상태일때 환불할 수 있습니다."),
    REFUND_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RFD-008", "Portone API 호출에 실패했습니다."),
    REFUND_PERIOD_EXPIRED(HttpStatus.BAD_REQUEST, "RFD-009", "환불 요청 가능 기간이 지났습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}