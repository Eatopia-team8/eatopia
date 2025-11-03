package org.example.eatopia.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSortBy {
    LATEST,
    PRICE_ASC,
    PRICE_DESC
}