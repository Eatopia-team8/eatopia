package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartCreateRequest(

        @NotNull
        Long productId,

        @NotNull
        @Min(value = 1)
        Integer quantity
) {
}
