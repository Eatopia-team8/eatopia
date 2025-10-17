package org.example.eatopia.domain.auth.exception;

import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
    UNAUTHORIZED_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USR-002", "이메일 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-003", "인증 정보가 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON-004", "해당 자원에 접근할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}
