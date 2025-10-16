package org.example.eatopia.domain.order.dto.request;

public record OrderCreateRequest(
        Long productId,
        Long sellerId
) {

}
