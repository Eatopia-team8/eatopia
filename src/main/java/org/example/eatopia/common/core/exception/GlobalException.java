package org.example.eatopia.common.core.exception;

import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final String code;

    //1. ErrorCode 객체를 받는 생성사
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
    }

    //2. HTTP Status와 메시지를 직접 지정하는 생성자
    public GlobalException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.errorCode = null;
        this.httpStatus = httpStatus;
        this.code = code;
    }
}