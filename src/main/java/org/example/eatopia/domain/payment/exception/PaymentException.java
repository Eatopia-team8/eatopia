package org.example.eatopia.domain.payment.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class PaymentException extends GlobalException {
    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}