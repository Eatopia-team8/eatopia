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
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "USR-004", "관리자만 사용할 수 있는 기능입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "USR-005", "유효하지 않거나 필수 입력값이 누락되었습니다."),
    USER_DONT_INPUT_COMPANY_NAME(HttpStatus.BAD_REQUEST, "USR-006", "구매자(BUYER)는 주소만 넣을 수 있습니다. 회사명은 업데이트할 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "USR-007", "주소 정보를 찾을 수 없습니다."),
    DUPLICATE_ADDRESS(HttpStatus.CONFLICT, "USR-008", "이미 등록된 주소입니다."),
    NO_CHANGES_DETECTED(HttpStatus.BAD_REQUEST, "USR-009", "변경된 내용이 없습니다."),
    PASSWORD_IS_SAME(HttpStatus.BAD_REQUEST, "USR-010", "새 비밀번호는 현재 비밀번호와 동일할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}