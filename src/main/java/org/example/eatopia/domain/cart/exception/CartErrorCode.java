package org.example.eatopia.domain.cart.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CRT-001", "존재하지 않는 장바구니입니다."),
    OUT_OF_STOCK(HttpStatus.CONFLICT, "CRT-002", "상품 재고가 부족합니다."),
    USER_CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CRT-003", "해당 사용자의 장바구니에 상품이 존재하지 않습니다"),
    CANNOT_DECREMENT(HttpStatus.BAD_REQUEST, "CRT-004", "장바구니 수량은 0 이하로 감소할 수 없습니다"),
    PRODUCT_NOT_FOR_SALE(HttpStatus.BAD_REQUEST, "CRT-005", "판매 상품이 아닙니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
