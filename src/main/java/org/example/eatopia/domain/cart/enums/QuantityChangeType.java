package org.example.eatopia.domain.cart.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuantityChangeType {
    INCREMENT, // 증가
    DECREMENT // 감소
}
