package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import org.example.eatopia.domain.cart.enums.QuantityChangeType;

public record CartUpdateQuantityRequest(

        @NotNull
        QuantityChangeType operation
) {
}
