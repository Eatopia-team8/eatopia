package org.example.eatopia.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CRT-001", "존재하지 않는 장바구니입니다."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "CRT-002", "상품 재고가 부족합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
