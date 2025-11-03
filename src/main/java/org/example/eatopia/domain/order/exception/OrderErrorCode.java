package org.example.eatopia.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD-001", "해당 주문을 찾을 수 없습니다."),
    INVALID_FINAL_PRICE(HttpStatus.BAD_REQUEST, "ORD-002", "최종 결제 금액은 0원 이상이어야 합니다."),
    ALREADY_CANCELED_ORDER(HttpStatus.BAD_REQUEST, "ORD-003", "이미 취소된 주문입니다."),
    CANNOT_CANCEL_ORDER(HttpStatus.BAD_REQUEST, "ORD-004", "주문을 취소할 수 없는 상태입니다."),
    CANNOT_SUCCESS_ORDER(HttpStatus.BAD_REQUEST, "ORD-005", "주문을 완료할 수 없는 상태입니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "ORD-006", "재고는 0 이상이어야 합니다."),
    EMPTY_CART_ORDER(HttpStatus.BAD_REQUEST, "ORD-007", "주문할 상품이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
