package org.example.eatopia.domain.category.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {
    CTG_NAME_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "CTG-001", "이미 존재하는 카테고리 이름입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
