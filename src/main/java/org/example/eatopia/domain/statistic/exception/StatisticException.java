package org.example.eatopia.domain.statistic.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class StatisticException extends GlobalException {
    public StatisticException(ErrorCode errorCode) {
        super(errorCode);
    }
}
