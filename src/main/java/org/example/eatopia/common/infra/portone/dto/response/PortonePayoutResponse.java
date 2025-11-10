package org.example.eatopia.common.infra.portone.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PortonePayoutResponse(
        Integer code,
        String message,
        PortonePayoutData response
) {
    public record PortonePayoutData(
            @JsonProperty("imp_uid") String impUid
    ) {
    }
}
