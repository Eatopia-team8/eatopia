package org.example.eatopia.domain.category.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class CategoryException extends GlobalException {
    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
