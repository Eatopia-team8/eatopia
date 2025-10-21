package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record CartItemSelectionRequest(

        @NotNull
        Boolean isSelected
) {
}
