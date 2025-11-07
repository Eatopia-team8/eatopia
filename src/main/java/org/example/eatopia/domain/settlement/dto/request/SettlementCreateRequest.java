package org.example.eatopia.domain.settlement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.settlement.enums.BankCode;

public record SettlementCreateRequest(

        @NotNull(message = "은행 코드는 필수입니다.")
        BankCode bankCode,

        @NotBlank(message = "계좌번호는 필수입니다.")
        String bankAccount,

        @NotBlank(message = "예금주명은 필수입니다.")
        String bankHolderName
) {
}