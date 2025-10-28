package org.example.eatopia.domain.review.dto.response;

import org.example.eatopia.domain.review.enums.ReviewStatus;

import java.time.LocalDateTime;

public record ReviewAdminResponse(
        Long id,
        Long userId,
        String userName,
        Long productId,
        String productName,
        String content,
        Integer rating,
        ReviewStatus status,
        LocalDateTime reportedAt,
        Integer reportCount,
        Long handledById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
