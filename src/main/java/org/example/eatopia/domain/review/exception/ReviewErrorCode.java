package org.example.eatopia.domain.review.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REV-001", "존재하지 않는 리뷰입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
