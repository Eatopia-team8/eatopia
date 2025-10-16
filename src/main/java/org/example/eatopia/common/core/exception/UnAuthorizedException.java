package org.example.eatopia.common.core.exception;

import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class UnAuthorizedException extends GlobalException {
    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
