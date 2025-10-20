package org.example.eatopia.domain.product.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class ProductException extends GlobalException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}