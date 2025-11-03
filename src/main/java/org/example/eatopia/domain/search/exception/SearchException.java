package org.example.eatopia.domain.search.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class SearchException extends GlobalException {
    public SearchException(ErrorCode errorCode) {
        super(errorCode);
    }
}
