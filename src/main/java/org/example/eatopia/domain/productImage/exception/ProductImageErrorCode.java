package org.example.eatopia.domain.productImage.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductImageErrorCode implements ErrorCode {
    PRD_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "PRD-IMG-001", "상품 이미지는 최소 1개 이상 필요합니다."),
    PRD_IMAGE_EXCEED_LIMIT(HttpStatus.BAD_REQUEST, "PRD-IMG-002", "상품 이미지는 최대 10개까지 등록 가능합니다."),
    PRD_INVALID_THUMBNAIL_COUNT(HttpStatus.BAD_REQUEST, "PRD-IMG-003", "대표 이미지는 정확히 1개만 지정해야 합니다."),
    PRD_DUPLICATE_IMAGE_ORDER(HttpStatus.BAD_REQUEST, "PRD-IMG-004", "이미지 순서가 중복되었습니다."),
    PRD_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRD-IMG-005", "이미지를 찾을 수 없습니다."),
    PRD_IMAGE_NOT_BELONG_TO_PRODUCT(HttpStatus.BAD_REQUEST, "PRD-IMG-006", "해당 상품의 이미지가 아닙니다."),
    PRD_IMAGE_LAST_ONE(HttpStatus.BAD_REQUEST, "PRD-IMG-007", "상품 이미지는 최소 1개 이상 필요합니다."),
    PRD_IMAGE_INVALID_DISPLAY_ORDER(HttpStatus.BAD_REQUEST, "PRD-IMG-008", "유효하지 않은 이미지 순서입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
