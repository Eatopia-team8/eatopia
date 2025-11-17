package org.example.eatopia.domain.review.dto.response;

import org.example.eatopia.domain.review.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewSellerResponse(
        Long id,
        Long productId,
        String productName,
        String userName,
        String content,
        int rating,
        ReviewStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime reportedAt,
        LocalDateTime deletedAt
) {
}
