package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartItemsSelectionUpdateRequest(

        @NotNull
        List<Long> productIds,
        boolean isSelected
) {
}