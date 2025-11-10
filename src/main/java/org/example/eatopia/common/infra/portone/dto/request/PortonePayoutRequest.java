package org.example.eatopia.common.infra.portone.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PortonePayoutRequest(
        @JsonProperty("merchant_uid") String merchantUid,
        BigDecimal amount,
        @JsonProperty("bank_code") String bankCode,
        @JsonProperty("bank_account") String bankAccount,
        @JsonProperty("bank_holder") String bankHolder
) {
}