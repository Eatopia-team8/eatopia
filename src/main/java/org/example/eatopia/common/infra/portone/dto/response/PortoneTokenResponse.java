package org.example.eatopia.common.infra.portone.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PortoneTokenResponse(
        Integer code,
        String message,
        PortoneTokenData response
) {
    public record PortoneTokenData(
            @JsonProperty("access_token") String accessToken
    ) {
    }
}
