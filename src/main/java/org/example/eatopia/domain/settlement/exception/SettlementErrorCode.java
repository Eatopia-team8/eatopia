package org.example.eatopia.domain.settlement.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements ErrorCode {
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SET-001", "정산 내역을 찾을 수 없습니다."),
    NOTHING_TO_SETTLE(HttpStatus.BAD_REQUEST, "SET-002", "정산할 내역이 없습니다."),
    INVALID_SETTLEMENT_STATUS(HttpStatus.BAD_REQUEST, "SET-003", "이미 처리되었거나 실패한 정산입니다."),
    PAYOUT_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SET-005", "Portone API 호출에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}