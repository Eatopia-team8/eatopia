package org.example.eatopia.domain.user.exception;

import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * User 도메인 관련 에러 코드를 정의하는 Enum
 */
@Getter
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USR-001", "이미 가입된 이메일입니다."),
    UNAUTHORIZED_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USR-002", "이메일 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR-003", "사용자 ID [{0}]를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "USR-004", "관리자만 사용할 수 있는 기능입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}