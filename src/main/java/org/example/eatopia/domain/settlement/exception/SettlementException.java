package org.example.eatopia.domain.settlement.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class SettlementException extends GlobalException {
    public SettlementException(ErrorCode errorCode) {
        super(errorCode);
    }
}