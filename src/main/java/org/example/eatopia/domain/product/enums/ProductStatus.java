package org.example.eatopia.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    AVAILABLE, // 판매중
    SOLD_OUT, // 품절
    HIDE // 숨김
}