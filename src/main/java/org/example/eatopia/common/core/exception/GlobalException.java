package org.example.eatopia.common.core.exception;

import lombok.Getter;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}