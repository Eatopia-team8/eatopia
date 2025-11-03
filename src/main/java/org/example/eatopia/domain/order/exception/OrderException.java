package org.example.eatopia.domain.order.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class OrderException extends GlobalException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
