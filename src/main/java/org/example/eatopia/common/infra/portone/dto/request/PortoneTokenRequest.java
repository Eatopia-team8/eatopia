package org.example.eatopia.common.infra.portone.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PortoneTokenRequest(
        @JsonProperty("imp_key") String impKey,
        @JsonProperty("imp_secret") String impSecret
) {
}