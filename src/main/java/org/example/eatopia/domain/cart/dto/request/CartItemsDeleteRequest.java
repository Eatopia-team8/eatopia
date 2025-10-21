package org.example.eatopia.domain.cart.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CartItemsDeleteRequest(

        @NotEmpty
        List<Long> productIds
) {
}