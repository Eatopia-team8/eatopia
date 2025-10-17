package org.example.eatopia.domain.cart.dto.response;

public record CartCreateResponse(
        String name
) {
    public static CartCreateResponse from(String name) {
        return new CartCreateResponse(
                name
        );
    }
}
