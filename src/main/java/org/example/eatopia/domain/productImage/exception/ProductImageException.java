package org.example.eatopia.domain.productImage.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class ProductImageException extends GlobalException {
    public ProductImageException(ErrorCode errorCode) {
        super(errorCode);
    }
}


