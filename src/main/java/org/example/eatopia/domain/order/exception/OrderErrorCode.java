package org.example.eatopia.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD-001", "해당 주문을 찾을 수 없습니다."),
    PRODUCT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "ORD-002", "상품 ID는 필수입니다."),
    SELLER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "ORD-003", "판매자 ID는 필수입니다."),
    INVALID_ORDER_REQUEST(HttpStatus.BAD_REQUEST, "ORD-004", "잘못된 주문 요청입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
