package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartCreateRequest(

        @NotNull
        Long productId,

        @Min(value = 1)
        int quantity
) {
}
