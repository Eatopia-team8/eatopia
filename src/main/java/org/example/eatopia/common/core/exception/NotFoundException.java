package org.example.eatopia.common.core.exception;

import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class NotFoundException extends GlobalException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
