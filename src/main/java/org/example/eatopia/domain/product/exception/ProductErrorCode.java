package org.example.eatopia.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRD_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD-001", "해당 상품을 찾을 수 없습니다."),
    PRD_NO_UPDATE_FIELDS(HttpStatus.BAD_REQUEST, "PRD-002", "수정할 항목이 하나 이상 필요합니다."),
    PRD_INVALID_PRICE(HttpStatus.BAD_REQUEST, "PRD-003", "가격은 0 이상이어야 합니다."),
    PRD_INVALID_STOCK(HttpStatus.BAD_REQUEST, "PRD-004", "재고는 0 이상이어야 합니다."),
    PRD_INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "PRD-005", "상위 카테고리에 상품을 등록할 수 없습니다."),
    PRD_NO_PERMISSION(HttpStatus.UNAUTHORIZED, "PRD-006", "상품 관리에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
