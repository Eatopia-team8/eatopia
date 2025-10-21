package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CartItemsSelectionRequest(

        @NotEmpty
        List<Long> productIds,

        @NotNull
        Boolean isSelected
) {
}