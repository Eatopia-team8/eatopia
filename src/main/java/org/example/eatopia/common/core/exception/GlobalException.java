package org.example.eatopia.common.core.exception;

import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    // 1. 기존 생성자: 인수가 없는 기본 형태
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    //2. 문제 해결을 위한 신규 생성자: 메시지 인수를 처리합니다.
    public GlobalException(ErrorCode errorCode, Object... messageArgs) {
        // messageArgs를 사용하여 에러 메시지의 플레이스홀더를 채웁니다.
        super(MessageFormat.format(errorCode.getMessage(), messageArgs));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getCode() {
        return errorCode.getCode();
    }
}
