package org.example.eatopia.domain.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.eatopia.domain.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long productId,
        String content,
        int rating,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime createdAt,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        LocalDateTime updatedAt
) {

    public static ReviewResponse fromForCreate(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt(),
                null
        );
    }
}
