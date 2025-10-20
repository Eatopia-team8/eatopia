package org.example.eatopia.domain.cart.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuantityChangeType {

    @JsonProperty("increment")
    INCREMENT(1), // 증가

    @JsonProperty("decrement")
    DECREMENT(-1); // 감소

    private final int quantityChange;

    public int apply(int quantity) {
        return quantity + quantityChange;
    }
}
