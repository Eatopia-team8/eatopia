package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CartSelectionRequest(

        @NotBlank
        boolean isSelected
) {
}
