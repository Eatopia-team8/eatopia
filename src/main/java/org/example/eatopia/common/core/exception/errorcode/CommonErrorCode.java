package org.example.eatopia.common.core.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 4xx: 클라이언트 오류
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "COMMON-001", "요청 값이 유효하지 않습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "COMMON-002", "요청 본문 형식이 잘못되었습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON-003", "인증 정보가 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMON-004", "해당 자원에 접근할 권한이 없습니다."),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-005", "요청한 자원을 찾을 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "COMMON-006", "이미 존재하는 데이터입니다."),

    // 5xx: 서버 오류
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-000", "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
