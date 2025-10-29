package org.example.eatopia.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REV-001", "존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REV-002", "리뷰는 중복 작성될 수 없습니다."),
    REVIEW_HIDDEN(HttpStatus.BAD_REQUEST, "REV-003", "숨김 처리된 리뷰는 볼 수 없습니다. 관리자에게 문의해주세요."),
    REVIEW_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "REV-004", "수정할 수 없는 리뷰입니다."),
    REVIEW_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "REV-005", "이미 삭제된 리뷰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
