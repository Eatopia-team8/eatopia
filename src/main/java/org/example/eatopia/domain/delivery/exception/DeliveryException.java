package org.example.eatopia.domain.delivery.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class DeliveryException extends GlobalException {
    public DeliveryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
