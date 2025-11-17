package org.example.eatopia.domain.auth.exception;

import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
    UNAUTHORIZED_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USR-002", "이메일 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-003", "인증 정보가 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON-004", "해당 자원에 접근할 권한이 없습니다."),

    INVALID_RESET_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-005", "유효하지 않거나 사용된 재설정 토큰입니다."),
    EXPIRED_RESET_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-006", "재설정 토큰이 만료되었습니다."),
    USER_IS_DELETED(HttpStatus.UNAUTHORIZED, "AUTH-007", "삭제된 사용자입니다."),
    USER_ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "AUTH-008", "이미 탈퇴 처리된 사용자입니다."),
    USER_ALREADY_LOGGED_OUT(HttpStatus.BAD_REQUEST, "AUTH-009", "이미 로그아웃된 상태입니다."),
    TOKEN_ALREADY_ISSUED(HttpStatus.TOO_MANY_REQUESTS, "AUTH-010", "이미 토큰이 발급되었습니다. 5분 뒤에 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AuthErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}
