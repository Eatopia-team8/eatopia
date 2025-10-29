package org.example.eatopia.domain.payment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY-001", "결제 정보를 찾을 수 없습니다."),
    CANNOT_CANCEL_PAYMENT(HttpStatus.BAD_REQUEST, "PAY-002", "결제를 취소할 수 없는 상태입니다."),
    PAYMENT_CANCELED_FAILED(HttpStatus.BAD_REQUEST, "PAY-003", "결제 취소 처리 중 오류가 발생했습니다."),
    ALREADY_PAID_ORDER(HttpStatus.CONFLICT, "PAY-004", "이미 결제가 완료된 주문입니다."),
    CANNOT_UPDATE_METHOD(HttpStatus.BAD_REQUEST, "PAY-005", "결제 수단을 변경할 수 없는 상태입니다."),

    PAYMENT_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PAY-006", "결제 API 연동 중 오류가 발생했습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.CONFLICT, "PAY-007", "결제 금액이 일치하지 않습니다."),
    INVALID_MERCHANT_UID(HttpStatus.NOT_FOUND, "PAY-008", "주문 정보를 찾을 수 없습니다."),
    PORTONE_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "PAY-009", "PortOne 결제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
