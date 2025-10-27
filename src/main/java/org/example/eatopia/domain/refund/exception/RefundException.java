package org.example.eatopia.domain.refund.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class RefundException extends GlobalException {
    public RefundException(ErrorCode errorCode) {
        super(errorCode);
    }
}