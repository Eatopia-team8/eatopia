package org.example.eatopia.domain.coupon.exception;

import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;

public class CouponIssueException extends GlobalException {
    public CouponIssueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
