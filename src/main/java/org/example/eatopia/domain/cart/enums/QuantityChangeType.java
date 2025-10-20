package org.example.eatopia.domain.cart.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuantityChangeType {

    @JsonProperty("increment")
    INCREMENT, // 증가

    @JsonProperty("decrement")
    DECREMENT // 감소
}
